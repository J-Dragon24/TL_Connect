package com.tl_connect.dev.modules.enroll.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.enroll.dto.ScheduleForCheckDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClassLog;
import com.tl_connect.dev.modules.enroll.repository.CourseClassLogRepository;
import com.tl_connect.dev.modules.enroll.repository.EnrollmentPeriodRepository;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;
import com.tl_connect.dev.shared.common.enums.EnrollAction;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.ForbiddenException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import com.tl_connect.dev.modules.course_class.CourseClass;
import com.tl_connect.dev.modules.course_class.CourseClassRepository;
import com.tl_connect.dev.modules.course_class.dto.CourseClassForEnrollDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollService {
    private final StudentCourseClassRepository studentCourseClassRepository;
    private final CourseClassLogRepository courseClassLogRepository;
    private final CourseClassRepository courseClassRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final PrerequisiteCheckService prerequisiteCheckService;
    private final StudentEnrollmentProfileService profileService;
    private final EnrollmentConditionService conditionService;
    private final StudentScheduleService studentScheduleService;
    private final ScheduleConflictService scheduleConflictService;
    private final EnrollmentPeriodService enrollmentPeriodService;

    @Transactional
    public void enroll(Long studentId, Long courseClassId, String studyProgramCode) {

        CourseClass courseClass = courseClassRepository.findById(courseClassId)
                .orElseThrow(() -> new NotFoundException("Course class not found"));

        checkEnrollmentPeriod(courseClass.getSemesterId());

        StudyProgramHeaderView header = studyProgramRepository
                .findByStudyProgramCodeAndStudentId(studyProgramCode, studentId)
                .orElseThrow(() -> new NotFoundException("Study program not found"));

        List<CourseClassForEnrollDTO> details = courseClassRepository.findDetailForEnrollmentById(courseClassId);

        if (details.isEmpty()) {
            throw new NotFoundException("Course class not found");
        }

        // check if student is allowed to enroll in the course class
        if (!studentCourseClassRepository.isSubjectAllowed(studentId, courseClass.getId())) {
            throw new ForbiddenException("You don't have permission to enroll in this course class");
        }

        // check if student has already enrolled in the course class
        Optional<StudentCourseClass> existingOpt = studentCourseClassRepository
                .findByStudentIdAndCourseClassId(studentId, courseClassId);

        StudentCourseClass scc = null;
        StudentCourseClassStatus oldStatus = null;

        if (existingOpt.isPresent()) {
            scc = existingOpt.get();
            oldStatus = scc.getStatus();

            if (Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED).contains(scc.getStatus())) {
                throw new ForbiddenException("You have already enrolled in this course class");
            }
        }

        boolean alreadyEnrollSameSubject = studentCourseClassRepository
                .existsByStudentIdAndSubjectIdAndSemesterIdAndStatusIn(
                        studentId,
                        courseClass.getSubjectId(),
                        courseClass.getSemesterId(),
                        Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED));

        if (alreadyEnrollSameSubject) {
            throw new ForbiddenException("You already enrolled this subject");
        }
        StudentEnrollmentProfile profile = profileService.getProfile(studentId, header.getId());

        boolean hasPassed = profile.getPassedSubjectIds().contains(courseClass.getSubjectId());

        if (hasPassed) {
            throw new ForbiddenException("You have already passed this subject");
        }

        boolean isRetake = profile.getFailedSubjectIds().contains(courseClass.getSubjectId());


        List<ScheduleForCheckDTO> newSchedules = details.stream()
                .map(ScheduleForCheckDTO::from)
                .toList();

        // check schedule conflict
        checkScheduleConflict(studentId, courseClass.getSemesterId(), newSchedules);
        // check subject condition
        checkSubjectCondition(profile, courseClass.getSubjectId());
        // check max credits
        checkMaxCredits(studentId, courseClass.getSemesterId(), details.get(0).getCredits());

        if (courseClass.getEnrolledCount() >= courseClass.getCapacity()) {
            throw new BadRequestException("Course class is full");
        }

        courseClass.setEnrolledCount(courseClass.getEnrolledCount() + 1);

        if (scc == null) {
            scc = new StudentCourseClass();
            scc.setStudentId(studentId);
            scc.setCourseClassId(courseClassId);
            scc.setSubjectId(courseClass.getSubjectId());
            scc.setSemesterId(courseClass.getSemesterId());
        }
        scc.setStatus(StudentCourseClassStatus.PENDING);
        scc.setIsRetake(isRetake);

        try {
            studentCourseClassRepository.save(scc);
        } catch (DataIntegrityViolationException e) {
            throw new ForbiddenException("Already enrolled");
        }

        StudentCourseClassLog log = StudentCourseClassLog.create(studentId, courseClassId, EnrollAction.ENROLL,
                oldStatus, scc.getStatus());
        courseClassLogRepository.save(log);

        List<ScheduleInterval> intervals = details.stream().map(c -> {
            return ScheduleInterval.builder()
                    .classScheduleId(c.getClassScheduleId())
                    .courseClassId(courseClassId)
                    .classCode(courseClass.getClassCode())
                    .dayOfWeek(c.getDayOfWeek())
                    .startPeriod(c.getStartPeriod())
                    .endPeriod(c.getEndPeriod())
                    .build();
        }).toList();

        studentScheduleService.addToCache(studentId, courseClass.getSemesterId(), intervals);
    }

    @Transactional
    public void drop(Long studentId, Long courseClassId) {

        StudentCourseClass scc = studentCourseClassRepository
            .findByStudentIdAndCourseClassId(studentId, courseClassId)
            .orElseThrow(() -> new NotFoundException("Enrollment not found"));

        if (!Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED)
                .contains(scc.getStatus())) {
            throw new ForbiddenException("Cannot drop this class");
        }

        StudentCourseClassStatus oldStatus = scc.getStatus();
        scc.setStatus(StudentCourseClassStatus.DROPPED);
        studentCourseClassRepository.save(scc);

        StudentCourseClassLog log = StudentCourseClassLog.create(
            studentId, courseClassId, EnrollAction.DROP, oldStatus, StudentCourseClassStatus.DROPPED
        );
        courseClassLogRepository.save(log);

        studentScheduleService.removeFromCache(studentId, scc.getSemesterId(), courseClassId);
    }
    

    private void checkSubjectCondition(StudentEnrollmentProfile profile, Long subjectId) {
        prerequisiteCheckService.check(subjectId, profile.getPassedSubjectIds());
        conditionService.check(subjectId, profile);
    }

    private void checkScheduleConflict(Long studentId, Long semesterId, List<ScheduleForCheckDTO> newSchedules) {
        scheduleConflictService.check(studentId, semesterId, newSchedules);
    }

    private void checkMaxCredits(Long studentId, Long semesterId, int newCredits) {
        Integer creditsRegistered = studentCourseClassRepository.findCreditsRegistered(studentId, semesterId);
        if (creditsRegistered + newCredits > 18) {
            throw new ForbiddenException("You have exceeded the maximum number of credits");
        }
    }

    private void checkEnrollmentPeriod(Long semesterId) {
    EnrollmentPeriod period = enrollmentPeriodService.getPeriod(semesterId);

    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(period.getStartTime())) {
        throw new ForbiddenException("Chưa đến thời gian đăng ký");
    }
    if (now.isAfter(period.getEndTime())) {
        throw new ForbiddenException("Đã hết thời gian đăng ký");
    }
}
}
