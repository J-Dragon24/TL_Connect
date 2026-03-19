package com.tl_connect.dev.modules.enroll.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;

@Repository
public interface StudentCourseClassRepository extends JpaRepository<StudentCourseClass, Long> {
    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM student_majors sm
            JOIN study_program_subjects sps 
                ON sm.study_program_id = sps.study_program_id
            JOIN course_classes cc 
                ON cc.subject_id = sps.subject_id
            WHERE sm.student_id = :studentId
            AND sm.is_primary = true
            AND cc.id = :courseClassId
        )
    """, nativeQuery = true)
    boolean isSubjectAllowed(Long studentId, Long courseClassId);

    Optional<StudentCourseClass> findByStudentIdAndCourseClassId(Long studentId, Long courseClassId);
}
