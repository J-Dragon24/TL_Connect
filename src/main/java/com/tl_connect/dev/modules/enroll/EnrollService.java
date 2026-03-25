package com.tl_connect.dev.modules.enroll;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.core.common.exception.ForbiddenException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.enroll.entity.CourseClass;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClassLog;
import com.tl_connect.dev.modules.enroll.repository.CourseClassLogRepository;
import com.tl_connect.dev.modules.enroll.repository.CourseClassRepository;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.core.common.enums.EnrollAction;
import com.tl_connect.dev.core.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.modules.subject.entity.SubjectEnrollmentCondition;
import com.tl_connect.dev.modules.subject.projection.SubjectPrerequisiteConditionRow;
import com.tl_connect.dev.modules.subject.repository.SubjectEnrollmentConditionRepository;
import com.tl_connect.dev.modules.subject.repository.SubjectRepository;
import com.tl_connect.dev.modules.academic_result.AcademicResultRepository;
import com.tl_connect.dev.modules.study_program.StudyProgramRepository;
import com.tl_connect.dev.modules.schedule.ScheduleRepository;
import com.tl_connect.dev.modules.schedule.entity.ClassSchedule;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollService {
    private final StudentCourseClassRepository studentCourseClassRepository;
    private final CourseClassLogRepository courseClassLogRepository;
    private final CourseClassRepository courseClassRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectEnrollmentConditionRepository subjectEnrollmentConditionRepository;
    private final AcademicResultRepository academicResultRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void enroll(Long studentId, Long courseClassId, String studyProgramCode) {

        CourseClass courseClass = courseClassRepository.findByIdForUpdate(courseClassId)
                .orElseThrow(() -> new NotFoundException("Course class not found"));

        StudyProgramHeaderView header = studyProgramRepository.findByStudyProgramCodeAndStudentId(studyProgramCode, studentId)
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
        Boolean latestPassed = academicResultRepository.getLatestSubjectResult(studentId, courseClass.getSubjectId());

        boolean hasPassed = (latestPassed != null && latestPassed);
        boolean isRetake = (latestPassed != null && !latestPassed);

        if (hasPassed) {
            throw new ForbiddenException("You have already passed this subject");
        }

        // check schedule conflict
        checkScheduleConflict(studentId, courseClass);
        // check subject condition
        checkSubjectCondition(studentId, courseClass, header.getId());
        // check max credits
        checkMaxCredits(studentId, courseClass, header.getId());

        int currentStudent = studentCourseClassRepository.countEnroll(courseClassId);
        if (currentStudent >= courseClass.getCapacity()) {
            throw new ForbiddenException("Course class is full");
        }

        if (scc == null) {
            scc = new StudentCourseClass();
            scc.setStudentId(studentId);
            scc.setCourseClassId(courseClassId);
        }
        scc.setStatus(StudentCourseClassStatus.PENDING);
        scc.setIsRetake(isRetake);

        try {
            studentCourseClassRepository.save(scc);
        } catch (DataIntegrityViolationException e) {
            throw new ForbiddenException("Already enrolled");
        }

        StudentCourseClassLog log = new StudentCourseClassLog();
        log.setStudentId(studentId);
        log.setCourseClassId(courseClassId);
        log.setAction(EnrollAction.ENROLL);
        log.setFromStatus(oldStatus);
        log.setToStatus(scc.getStatus());
        courseClassLogRepository.save(log);
    }

    public void preCheck(Long studentId, CourseClass courseClass, Long studyProgramId) {
        
    }

    private void checkSubjectCondition(Long studentId, CourseClass courseClass, Long studyProgramId) {
        List<SubjectPrerequisiteConditionRow> groups = subjectRepository.findSubjectPrerequisiteCondition(studentId,
                courseClass.getId());

        if (groups != null && !groups.isEmpty()) {
            for (SubjectPrerequisiteConditionRow group : groups) {
                if (group.getPassedCount() < group.getMinSubjectsRequired()) {
                    throw new ForbiddenException(group.getDescription() != null ? group.getDescription()
                            : "You don't have permission to enroll in this course class");
                }
            }
        }

        List<SubjectEnrollmentCondition> conditions = subjectEnrollmentConditionRepository
                .findBySubjectId(courseClass.getSubjectId());

        if (conditions != null && !conditions.isEmpty()) {
            for (SubjectEnrollmentCondition condition : conditions) {
                switch (condition.getConditionType()) {
                    case MIN_CREDITS:
                        Integer creditsPassed = academicResultRepository.findCreditsPassed(studentId, studyProgramId);
                        if (creditsPassed < condition.getConditionValue().intValue()) {
                            throw new ForbiddenException("You don't have permission to enroll in this course class");
                        }
                        break;
                    case GPA:
                        BigDecimal gpa = academicResultRepository.findCurrentSemesterGpa(studentId, studyProgramId);
                        if (gpa == null || gpa.compareTo(condition.getConditionValue()) < 0) {
                            throw new ForbiddenException("You don't have permission to enroll in this course class");
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void checkScheduleConflict(Long studentId, CourseClass courseClass) {
        List<StudentCourseClass> registered = studentCourseClassRepository.findByStudentIdAndSemesterIdAndStatusIn(
                studentId,
                courseClass.getSemesterId(),
                Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED));

        if (registered.isEmpty())
            return;

        List<ClassSchedule> newSchedules = scheduleRepository.findByCourseClassId(courseClass.getId());

        if (newSchedules.isEmpty()) {
            throw new ForbiddenException("Course class has no schedule");
        }

        List<Long> registeredClassIds = registered.stream()
                .map(StudentCourseClass::getCourseClassId)
                .filter(id -> !id.equals(courseClass.getId()))
                .toList();

        if (registeredClassIds.isEmpty())
            return;

        boolean isConflict = scheduleRepository.isScheduleConflict(courseClass.getId(), registeredClassIds);

        if (isConflict) {
            throw new ForbiddenException("You have a schedule conflict");
        }
    }

    private void checkMaxCredits(Long studentId, CourseClass courseClass, Long studyProgramId) {
        Integer creditsRegistered = studentCourseClassRepository.findCreditsRegistered(studentId,
                courseClass.getSemesterId());
        Integer newCredits = subjectRepository.findById(courseClass.getSubjectId())
                .orElseThrow(() -> new NotFoundException("Subject not found")).getCredits();
        if (creditsRegistered + newCredits > 18) {
            throw new ForbiddenException("You have exceeded the maximum number of credits");
        }
    }
}
