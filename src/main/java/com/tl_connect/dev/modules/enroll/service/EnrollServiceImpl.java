package com.tl_connect.dev.modules.enroll.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.enroll.dto.CourseClassForEnrollDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollViewDTO;
import com.tl_connect.dev.modules.enroll.dto.ScheduleForCheckDTO;
import com.tl_connect.dev.modules.enroll.dto.ScheduleForEnrollDTO;
import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;
import com.tl_connect.dev.modules.enroll.dto.SubjectForEnrollDTO;
import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClassLog;
import com.tl_connect.dev.modules.enroll.projection.CourseClassForEnrollRow;
import com.tl_connect.dev.modules.enroll.projection.DetailsForCheckEnrollRow;
import com.tl_connect.dev.modules.enroll.projection.SubjectForEnrollRow;
import com.tl_connect.dev.modules.enroll.repository.CourseClassLogRepository;
import com.tl_connect.dev.modules.enroll.service.interfaces.EnrollService;
import com.tl_connect.dev.modules.enroll.service.interfaces.EnrollmentConditionService;
import com.tl_connect.dev.modules.enroll.service.interfaces.StudentCourseClassService;
import com.tl_connect.dev.modules.lecturer.dto.LecturerDTO;
import com.tl_connect.dev.modules.schedule.ScheduleRepository;
import com.tl_connect.dev.modules.schedule.dto.ScheduleCourseClassDTO;
import com.tl_connect.dev.modules.schedule.projection.ScheduleRow;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.modules.study_program.service.interfaces.StudyProgramService;
import com.tl_connect.dev.shared.common.enums.EnrollAction;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.common.exception.DuplicateRegistrationException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.ForbiddenException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

import com.tl_connect.dev.modules.course_class.CourseClass;
import com.tl_connect.dev.modules.course_class.service.interfaces.CourseClassService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollServiceImpl implements EnrollService {
    private final StudentCourseClassService studentCourseClassService;
    private final CourseClassLogRepository courseClassLogRepository;
    private final CourseClassService courseClassService;
    private final StudyProgramService studyProgramService;
    private final PrerequisiteCheckService prerequisiteCheckService;
    private final StudentEnrollmentProfileService profileService;
    private final EnrollmentConditionService conditionService;
    private final StudentScheduleService studentScheduleService;
    private final ScheduleConflictService scheduleConflictService;
    private final EnrollmentPeriodServiceImpl enrollmentPeriodService;
    private final ScheduleRepository scheduleRepository;

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

    @Transactional
    public void enroll(Long studentId, Long courseClassId, Long studyProgramId) {

        CourseClass courseClass = courseClassService.findById(courseClassId);

        EnrollmentPeriod period = enrollmentPeriodService.getPeriod();

        boolean isPeriodValid = checkEnrollmentPeriod(period);

        if (!isPeriodValid) {
            throw new DuplicateRegistrationException(ResponseStatus.OUTSIDE_REGISTRATION_PERIOD,
                    "The registration period has not started yet or has ended");
        }

        List<DetailsForCheckEnrollRow> details = courseClassService.findDetailForEnrollmentById(courseClassId);

        if (details.isEmpty()) {
            throw new NotFoundException("Course class not found");
        }

        // check if student is allowed to enroll in the course class
        if (!studentCourseClassService.isSubjectAllowedForEnrollment(studentId, courseClass.getId())) {
            throw new ErrorException(ResponseStatus.SUBJECT_NOT_IN_PROGRAM,
                    "You don't have permission to enroll in this subject");
        }

        // check if student has already enrolled in the course class
        Optional<StudentCourseClass> existingOpt = studentCourseClassService
                .findByStudentIdAndCourseClassId(studentId, courseClassId);

        StudentCourseClass scc = null;
        StudentCourseClassStatus oldStatus = null;

        if (existingOpt.isPresent()) {
            scc = existingOpt.get();
            oldStatus = scc.getStatus();

            if (Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED).contains(scc.getStatus())) {
                throw new DuplicateRegistrationException(ResponseStatus.DUPLICATE_COURSE_CLASS,
                        "You have already enrolled in this course class");
            }
        }

        boolean alreadyEnrollSameSubject = studentCourseClassService
                .existsByStudentIdAndSubjectIdAndSemesterIdAndStatusIn(
                        studentId,
                        courseClass.getSubjectId(),
                        courseClass.getSemesterId(),
                        Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED));

        if (alreadyEnrollSameSubject) {
            throw new DuplicateRegistrationException(ResponseStatus.DUPLICATE_SUBJECT,
                    "You already enrolled this subject");
        }
        StudentEnrollmentProfile profile = profileService.getProfile(studentId, studyProgramId);

        boolean hasPassed = profile.getPassedSubjectIds().contains(courseClass.getSubjectId());

        if (hasPassed) {
            throw new DuplicateRegistrationException(ResponseStatus.SUBJECT_ALREADY_PASSED,
                    "You have already passed this subject");
        }

        boolean isRetake = profile.getFailedSubjectIds().contains(courseClass.getSubjectId());

        System.out.println("details: " + details);
        List<ScheduleForCheckDTO> newSchedules = details.stream()
                .map(ScheduleForCheckDTO::from)
                .toList();

        // check schedule conflict
        checkScheduleConflict(studentId, courseClass.getSemesterId(), newSchedules);
        // check subject condition
        checkSubjectCondition(profile, courseClass.getSubjectId());
        // check max credits
        checkMaxCredits(studentId, courseClass.getSemesterId(), details.get(0).getCredits(), period.getMaxCredits());

        if (courseClass.getEnrolledCount() >= courseClass.getCapacity()) {
            throw new ErrorException(ResponseStatus.CLASS_FULL, "Course class is full");
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
            studentCourseClassService.save(scc);
        } catch (DataIntegrityViolationException e) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR, "Already enrolled");
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

        StudentCourseClass scc = studentCourseClassService
                .findByStudentIdAndCourseClassId(studentId, courseClassId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found"));

        if (!Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED)
                .contains(scc.getStatus())) {
            throw new ForbiddenException("Cannot drop this class");
        }

        StudentCourseClassStatus oldStatus = scc.getStatus();
        scc.setStatus(StudentCourseClassStatus.DROPPED);
        studentCourseClassService.save(scc);

        StudentCourseClassLog log = StudentCourseClassLog.create(
                studentId, courseClassId, EnrollAction.DROP, oldStatus, StudentCourseClassStatus.DROPPED);
        courseClassLogRepository.save(log);

        studentScheduleService.removeFromCache(studentId, scc.getSemesterId(), courseClassId);
    }

    public List<ScheduleCourseClassDTO> getTempSchedule(Long studentId, Long semesterId) {
        List<ScheduleRow> scheduleRows = scheduleRepository.findTempSchedule(studentId,
                semesterId);

        List<ScheduleCourseClassDTO> courseClasses = scheduleRows.stream()
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

        return courseClasses;
    }



    private void checkSubjectCondition(StudentEnrollmentProfile profile, Long subjectId) {
        prerequisiteCheckService.check(subjectId, profile.getPassedSubjectIds());
        conditionService.check(subjectId, profile);
    }

    private void checkScheduleConflict(Long studentId, Long semesterId, List<ScheduleForCheckDTO> newSchedules) {
        scheduleConflictService.check(studentId, semesterId, newSchedules);
    }

    private void checkMaxCredits(Long studentId, Long semesterId, int newCredits, int maxCredits) {
        Integer creditsRegistered = studentCourseClassService.findCreditsRegistered(studentId, semesterId);
        if (creditsRegistered + newCredits > maxCredits) {
            throw new ErrorException(ResponseStatus.MAX_CREDIT_EXCEEDED,
                    "You have exceeded the maximum number of credits");
        }
    }

    private boolean checkEnrollmentPeriod(EnrollmentPeriod period) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(period.getStartTime()) || now.isAfter(period.getEndTime())) {
            return false;
        }
        return true;
    }
}
