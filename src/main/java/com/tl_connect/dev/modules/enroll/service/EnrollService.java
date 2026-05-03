package com.tl_connect.dev.modules.enroll.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.enroll.dto.ScheduleForCheckDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClassLog;
import com.tl_connect.dev.modules.enroll.repository.CourseClassLogRepository;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.modules.study_program.repository.StudyProgramRepository;
import com.tl_connect.dev.shared.common.enums.EnrollAction;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.common.exception.ForbiddenException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;
import com.tl_connect.dev.modules.academic_result.repository.StudentSubjectResultRepository;
import com.tl_connect.dev.modules.course_class.CourseClassRepository;
import com.tl_connect.dev.modules.course_class.dto.CourseClassForEnrollDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollService {
    private final StudentCourseClassRepository studentCourseClassRepository;
    private final CourseClassLogRepository courseClassLogRepository;
    private final CourseClassRepository courseClassRepository;
    private final StudentSubjectResultRepository studentSubjectResultRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final PrerequisiteCheckService prerequisiteCheckService;
    private final StudentEnrollmentProfileService profileService;
    private final EnrollmentConditionService conditionService;
    private final StudentScheduleService studentScheduleService;
    private final ScheduleConflictService scheduleConflictService;

    @Transactional
    public void enroll(Long studentId, Long courseClassId, String studyProgramCode) {

        List<CourseClassForEnrollDTO> courseClasses = courseClassRepository.findDetailForEnrollmentById(courseClassId);

        if (courseClasses.isEmpty()) {
            throw new NotFoundException("Course class not found");
        }

        CourseClassForEnrollDTO courseClass = courseClasses.get(0);

        StudyProgramHeaderView header = studyProgramRepository
                .findByStudyProgramCodeAndStudentId(studyProgramCode, studentId)
                .orElseThrow(() -> new NotFoundException("Study program not found"));

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

        // check if student has passed the subject
        Boolean latestPassed = studentSubjectResultRepository.getLatestSubjectResult(studentId,
                courseClass.getSubjectId());

        boolean hasPassed = (latestPassed != null && latestPassed);
        boolean isRetake = (latestPassed != null && !latestPassed);

        if (hasPassed) {
            throw new ForbiddenException("You have already passed this subject");
        }

        StudentEnrollmentProfile profile = profileService.getProfile(studentId, header.getId());
        List<ScheduleForCheckDTO> newSchedules = courseClasses.stream()
                .map(ScheduleForCheckDTO::from)
                .toList();

        // check schedule conflict
        checkScheduleConflict(studentId, courseClass.getSemesterId(), newSchedules);
        // check subject condition
        checkSubjectCondition(profile, courseClass.getSubjectId());
        // check max credits
        checkMaxCredits(studentId, courseClass.getSemesterId(), courseClass.getCredits());

        int currentStudent = studentCourseClassRepository.countEnroll(courseClassId);
        if (currentStudent >= courseClass.getCapacity()) {
            throw new ForbiddenException("Course class is full");
        }

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

        List<ScheduleInterval> intervals = courseClasses.stream().map(c -> {
            return ScheduleInterval.builder()
                    .classScheduleId(c.getClassScheduleId())
                    .courseClassId(courseClassId)
                    .classCode(c.getClassCode())
                    .dayOfWeek(c.getDayOfWeek())
                    .startPeriod(c.getStartPeriod())
                    .endPeriod(c.getEndPeriod())
                    .build();
        }).toList();

        studentScheduleService.addToCache(studentId, courseClass.getSemesterId(), intervals);
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
}
