package com.tl_connect.dev.modules.enroll.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.course_class.CourseClass;
import com.tl_connect.dev.modules.course_class.service.interfaces.CourseClassService;
import com.tl_connect.dev.modules.enroll.dto.EnrollmentValidationResult;
import com.tl_connect.dev.modules.enroll.dto.ScheduleForCheckDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.projection.DetailsForCheckEnrollRow;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.common.exception.DuplicateRegistrationException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Gom toàn bộ "read-heavy phase" vào một chỗ.
 *
 * <p>Thực hiện tất cả validation queries TRƯỚC khi bắt đầu write transaction,
 * giúp giữ transaction ngắn nhất có thể (chỉ còn: check+update enrolled_count
 * và insert enrollment).
 *
 * <p>Trả về {@link EnrollmentValidationResult} chứa đủ dữ liệu để transaction
 * write có thể chạy mà không cần thêm DB call nào.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentPreCheckService {

    private final CourseClassService courseClassService;
    private final StudentCourseClassService studentCourseClassService;
    private final StudentEnrollmentProfileService profileService;
    private final PrerequisiteCheckService prerequisiteCheckService;
    private final ScheduleConflictService scheduleConflictService;
    private final EnrollmentConditionServiceImpl conditionService;


    public EnrollmentValidationResult validate(
            Long studentId,
            Long courseClassId,
            Long studyProgramId,
            EnrollmentPeriod period) {

        // 1. Load course class details (schedule + credits) — 1 query
        List<DetailsForCheckEnrollRow> details = courseClassService.findDetailForEnrollmentById(courseClassId);
        if (details.isEmpty()) {
            throw new NotFoundException("Course class not found");
        }

        // 2. Load CourseClass entity để lấy subjectId, semesterId, classCode — 1 query (cached by JPA L1)
        CourseClass courseClass = courseClassService.findById(courseClassId);

        // 3. Kiểm tra subject có thuộc chương trình đào tạo không — 1 query
        if (!studentCourseClassService.isSubjectAllowedForEnrollment(studentId, courseClass.getId())) {
            throw new ErrorException(ResponseStatus.SUBJECT_NOT_IN_PROGRAM,
                    "You don't have permission to enroll in this subject");
        }

        // 4. Kiểm tra đăng ký trùng course class — 1 query
        Optional<StudentCourseClass> existingOpt =
                studentCourseClassService.findByStudentIdAndCourseClassId(studentId, courseClassId);

        StudentCourseClass existingScc = null;
        StudentCourseClassStatus oldStatus = null;

        if (existingOpt.isPresent()) {
            existingScc = existingOpt.get();
            oldStatus = existingScc.getStatus();
            if (Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED)
                    .contains(existingScc.getStatus())) {
                throw new DuplicateRegistrationException(ResponseStatus.DUPLICATE_COURSE_CLASS,
                        "You have already enrolled in this course class");
            }
        }

        // 5. Kiểm tra đăng ký trùng subject cùng kỳ — 1 query
        boolean alreadyEnrolledSameSubject = studentCourseClassService
                .existsByStudentIdAndSubjectIdAndSemesterIdAndStatusIn(
                        studentId,
                        courseClass.getSubjectId(),
                        courseClass.getSemesterId(),
                        Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED));
        if (alreadyEnrolledSameSubject) {
            throw new DuplicateRegistrationException(ResponseStatus.DUPLICATE_SUBJECT,
                    "You already enrolled this subject");
        }

        // 6. Load student profile (có cache Redis) — tối đa 1 DB query nếu cache miss
        StudentEnrollmentProfile profile = profileService.getProfile(studentId, studyProgramId);

        // 7. Kiểm tra đã qua môn chưa
        if (profile.getPassedSubjectIds().contains(courseClass.getSubjectId())) {
            throw new DuplicateRegistrationException(ResponseStatus.SUBJECT_ALREADY_PASSED,
                    "You have already passed this subject");
        }

        List<ScheduleForCheckDTO> newSchedules = details.stream()
                .map(ScheduleForCheckDTO::from)
                .toList();

        scheduleConflictService.check(studentId, courseClass.getSemesterId(), newSchedules);

        // 9. Kiểm tra điều kiện môn học (prerequisite + GPA/credits condition) — tối đa 2 queries
        prerequisiteCheckService.check(courseClass.getSubjectId(), profile.getPassedSubjectIds());
        conditionService.check(courseClass.getSubjectId(), profile);

        // 10. Kiểm tra tín chỉ tối đa — 1 query
        int creditsRegistered = studentCourseClassService.findCreditsRegistered(studentId, courseClass.getSemesterId());
        int newCredits = details.get(0).getCredits();
        if (creditsRegistered + newCredits > period.getMaxCredits()) {
            throw new ErrorException(ResponseStatus.MAX_CREDIT_EXCEEDED,
                    "You have exceeded the maximum number of credits");
        }

        // Build ScheduleIntervals để dùng sau khi enroll (push vào cache)
        List<ScheduleInterval> intervals = details.stream()
                .map(c -> ScheduleInterval.builder()
                        .classScheduleId(c.getClassScheduleId())
                        .courseClassId(courseClassId)
                        .classCode(courseClass.getClassCode())
                        .dayOfWeek(c.getDayOfWeek())
                        .startPeriod(c.getStartPeriod())
                        .endPeriod(c.getEndPeriod())
                        .build())
                .toList();

        boolean isRetake = profile.getFailedSubjectIds().contains(courseClass.getSubjectId());

        return EnrollmentValidationResult.builder()
                .existingScc(existingScc)
                .oldStatus(oldStatus)
                .isRetake(isRetake)
                .credits(newCredits)
                .subjectId(courseClass.getSubjectId())
                .semesterId(courseClass.getSemesterId())
                .details(details)
                .scheduleIntervals(intervals)
                .build();
    }
}
