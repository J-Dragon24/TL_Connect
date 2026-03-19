package com.tl_connect.dev.modules.enroll;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.core.common.exception.ForbiddenException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.enroll.entity.CourseClass;
import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.repository.CourseClassLogRepository;
import com.tl_connect.dev.modules.enroll.repository.CourseClassRepository;
import com.tl_connect.dev.modules.enroll.repository.StudentCourseClassRepository;
import com.tl_connect.dev.core.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.modules.subject.projection.SubjectPrerequisiteConditionRow;
import com.tl_connect.dev.modules.subject.repository.SubjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollService {
    private final StudentCourseClassRepository studentCourseClassRepository;
    private final CourseClassLogRepository courseClassLogRepository;
    private final CourseClassRepository courseClassRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public void enroll(Long studentId, Long courseClassId) {

        CourseClass courseClass = courseClassRepository.findById(courseClassId)
                .orElseThrow(() -> new NotFoundException("Course class not found"));


        Optional<StudentCourseClass> existingOpt = studentCourseClassRepository
                .findByStudentIdAndCourseClassId(studentId, courseClassId);

        StudentCourseClass scc = null;
        StudentCourseClassStatus oldStatus = null;

        if (existingOpt.isPresent()) {
            scc = existingOpt.get();
            oldStatus = scc.getStatus();

            if (Set.of(StudentCourseClassStatus.PENDING, StudentCourseClassStatus.ENROLLED).contains(scc.getStatus())) {
                throw new RuntimeException("You have already enrolled in this course class");
            }
        }

        checkSubjectCondition(studentId, courseClassId, courseClass);

        
    }

    private void checkSubjectCondition(Long studentId, Long courseClassId, CourseClass courseClass) {
        if (!studentCourseClassRepository.isSubjectAllowed(studentId, courseClassId)) {
            throw new ForbiddenException("You don't have permission to enroll in this course class");
        }

        List<SubjectPrerequisiteConditionRow> groups = subjectRepository.findSubjectPrerequisiteCondition(studentId, courseClassId);
        
        for (SubjectPrerequisiteConditionRow group : groups) {
            if (group.getPassedCount() < group.getMinSubjectsRequired()) {
                throw new ForbiddenException("You don't have permission to enroll in this course class");
            }
        }

    }
}
