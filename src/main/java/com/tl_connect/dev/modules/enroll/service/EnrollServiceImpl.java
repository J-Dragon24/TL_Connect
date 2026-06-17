package com.tl_connect.dev.modules.enroll.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.course_class.service.interfaces.CourseClassService;
import com.tl_connect.dev.modules.enroll.dto.CourseClassForEnrollDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollViewDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollmentHeaderDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollmentValidationResult;
import com.tl_connect.dev.modules.enroll.dto.ScheduleForEnrollDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;
import com.tl_connect.dev.modules.enroll.dto.SubjectForEnrollDTO;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.event.EnrollmentCreatedEvent;
import com.tl_connect.dev.modules.enroll.event.EnrollmentDroppedEvent;
import com.tl_connect.dev.modules.enroll.projection.CourseClassForEnrollRow;
import com.tl_connect.dev.modules.enroll.projection.SubjectForEnrollRow;
import com.tl_connect.dev.modules.enroll.service.interfaces.EnrollService;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.modules.lecturer.dto.LecturerDTO;
import com.tl_connect.dev.modules.schedule.ScheduleRepository;
import com.tl_connect.dev.modules.schedule.dto.ScheduleCourseClassDTO;
import com.tl_connect.dev.modules.schedule.projection.ScheduleRow;
import com.tl_connect.dev.modules.semester.Semester;
import com.tl_connect.dev.modules.semester.service.interfaces.SemesterService;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.modules.study_program.service.interfaces.StudyProgramService;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.common.exception.DuplicateRegistrationException;
import com.tl_connect.dev.shared.common.exception.ForbiddenException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollServiceImpl implements EnrollService {

    private final StudentCourseClassService studentCourseClassService;
    private final CourseClassService courseClassService;
    private final StudyProgramService studyProgramService;
    private final StudentEnrollmentProfileService profileService;
    private final EnrollmentPeriodServiceImpl enrollmentPeriodService;
    private final ScheduleRepository scheduleRepository;
    private final EnrollmentPreCheckService preCheckService;
    private final EnrollTransactionService enrollTransactionService;
    private final ApplicationEventPublisher eventPublisher;
    private final SemesterService semesterService;

    // -------------------------------------------------------------------------
    // Read-only endpoints
    // -------------------------------------------------------------------------

    public EnrollmentHeaderDTO getEnrollmentPeriods(Long studentId, String studyProgramCode) {
        EnrollmentPeriod period = enrollmentPeriodService.getPeriod();

        Semester semester = semesterService.findByIdAndIsActive(period.getSemesterId());

        return EnrollmentHeaderDTO.builder()
                .semesterId(period.getSemesterId())
                .semesterName(semester.getSemesterName())
                .semesterCode(semester.getSemesterCode())
                .startTime(period.getStartTime())
                .endTime(period.getEndTime())
                .build();
    }

    public EnrollViewDTO getAvailableSubjects(Long studentId, String studyProgramCode) {

        StudyProgramHeaderView header = studyProgramService
                .findByStudyProgramCodeAndStudentId(studyProgramCode, studentId);

        EnrollmentPeriod period = enrollmentPeriodService.getPeriod();
        boolean isPeriodValid = checkEnrollmentPeriod(period);

        List<SubjectForEnrollDTO> subjectForEnrollDTOS = new ArrayList<>();

        if (isPeriodValid) {
            StudentEnrollmentProfile profile = profileService.getProfile(studentId, header.getId());
            List<SubjectForEnrollRow> subjects = studyProgramService.findSubjectsByStudyProgramId(header.getId());
            subjectForEnrollDTOS = subjects.stream()
                    .filter(subject -> !profile.getPassedSubjectIds().contains(subject.getSubjectId()))
                    .map(SubjectForEnrollDTO::from).toList();
        }

        return EnrollViewDTO.builder()
                .studyProgramId(header.getId())
                .studyProgramCode(header.getStudyProgramCode())
                .studyProgramName(header.getStudyProgramName())
                .semesterId(period.getSemesterId())
                .startTime(period.getStartTime())
                .endTime(period.getEndTime())
                .subjects(subjectForEnrollDTOS)
                .build();
    }

    public List<CourseClassForEnrollDTO> getAvailableCourseClasses(Long subjectId, Long semesterId) {
        List<CourseClassForEnrollRow> rows = courseClassService.findCourseClassForEnrollment(subjectId, semesterId);
        Map<Long, CourseClassForEnrollDTO> map = new LinkedHashMap<>();
        for (CourseClassForEnrollRow row : rows) {
            map.computeIfAbsent(row.getId(), id -> CourseClassForEnrollDTO.builder()
                    .id(row.getId())
                    .lecturerCode(row.getLecturerCode())
                    .lecturerName(row.getLecturerName())
                    .classCode(row.getClassCode())
                    .className(row.getClassName())
                    .capacity(row.getCapacity())
                    .enrolledCount(row.getEnrolledCount())
                    .schedules(new ArrayList<>())
                    .build());

            map.get(row.getId()).getSchedules().add(
                    ScheduleForEnrollDTO.builder()
                            .dayOfWeek(row.getDayOfWeek())
                            .startPeriod(row.getStartPeriod())
                            .endPeriod(row.getEndPeriod())
                            .startTime(row.getStartTime())
                            .endTime(row.getEndTime())
                            .room(row.getRoom())
                            .build());
        }
        return new ArrayList<>(map.values());
    }

    // -------------------------------------------------------------------------
    // Enroll — 3-phase pattern
    // -------------------------------------------------------------------------

    public void enroll(Long studentId, Long courseClassId, Long studyProgramId) {

        EnrollmentPeriod period = enrollmentPeriodService.getPeriod();

        if (!checkEnrollmentPeriod(period)) {
            throw new DuplicateRegistrationException(ResponseStatus.OUTSIDE_REGISTRATION_PERIOD,
                    "The registration period has not started yet or has ended");
        }

        EnrollmentValidationResult validation = preCheckService.validate(
                studentId, courseClassId, studyProgramId, period);

        StudentCourseClass saved = enrollTransactionService.execute(studentId, courseClassId, validation);

        eventPublisher.publishEvent(new EnrollmentCreatedEvent(
                studentId,
                courseClassId,
                saved.getSemesterId(),
                validation.getOldStatus(),
                saved.getStatus(),
                validation.getScheduleIntervals()));
    }

    // -------------------------------------------------------------------------
    // Drop
    // -------------------------------------------------------------------------

    @Transactional
    public void drop(Long studentId, Long courseClassId) {

        StudentCourseClass scc = studentCourseClassService
                .findByStudentIdAndCourseClassId(studentId, courseClassId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found"));

        if (!Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED)
                .contains(scc.getStatus())) {
            throw new ForbiddenException("Cannot drop this class");
        }

        StudentCourseClassStatus oldStatus = scc.getStatus();
        Long semesterId = scc.getSemesterId();

        scc.setStatus(StudentCourseClassStatus.DROPPED);
        studentCourseClassService.save(scc);
        courseClassService.decreaseEnrolledCount(courseClassId);

        // Publish event — side effects chạy async sau COMMIT
        eventPublisher.publishEvent(new EnrollmentDroppedEvent(
                studentId,
                courseClassId,
                semesterId,
                oldStatus,
                StudentCourseClassStatus.DROPPED));
    }

    // -------------------------------------------------------------------------
    // Schedule view
    // -------------------------------------------------------------------------

    public List<ScheduleCourseClassDTO> getTempSchedule(Long studentId, Long semesterId) {
        List<ScheduleRow> scheduleRows = scheduleRepository.findTempSchedule(studentId, semesterId);

        return scheduleRows.stream()
                .map(row -> {
                    LecturerDTO lecturer = LecturerDTO.builder()
                            .fullName(row.getLecturerName())
                            .email(row.getLecturerEmail())
                            .phoneNumber(row.getLecturerPhone())
                            .lecturerCode(row.getLecturerCode())
                            .build();
                    return ScheduleCourseClassDTO.builder()
                            .classCode(row.getClassCode())
                            .dayOfWeek(row.getDayOfWeek())
                            .subjectName(row.getSubjectName())
                            .subjectCode(row.getSubjectCode())
                            .startPeriod(row.getStartPeriod())
                            .endPeriod(row.getEndPeriod())
                            .credits(row.getCredits())
                            .startTime(row.getStartTime())
                            .endTime(row.getEndTime())
                            .room(row.getRoom())
                            .lecturer(lecturer)
                            .build();
                }).collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean checkEnrollmentPeriod(EnrollmentPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(period.getStartTime()) && !now.isAfter(period.getEndTime());
    }
}
