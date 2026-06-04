package com.tl_connect.dev.modules.enroll.service.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;

public interface StudentCourseClassService {
    List<ScheduleInterval> findCurrentSchedule(Long studentId, Long semesterId);

    boolean isSubjectAllowedForEnrollment(Long studentId, Long courseClassId);

    Optional<StudentCourseClass> findByStudentIdAndCourseClassId(Long studentId, Long courseClassId);

    boolean existsByStudentIdAndSubjectIdAndSemesterIdAndStatusIn(
        Long studentId,
        Long subjectId,
        Long semesterId,
        Set<StudentCourseClassStatus> statusSet
    );

    StudentCourseClass save(StudentCourseClass entity);

    void delete(StudentCourseClass entity);

    Integer findCreditsRegistered(Long studentId, Long semesterId);

    List<StudentCourseClass> findByCourseClassId(Long courseClassId);
}
