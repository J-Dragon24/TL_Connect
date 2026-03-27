package com.tl_connect.dev.modules.enroll.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.core.common.enums.StudentCourseClassStatus;
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
    boolean isSubjectAllowed(@Param("studentId") Long studentId, @Param("courseClassId") Long courseClassId);

    Optional<StudentCourseClass> findByStudentIdAndCourseClassId(Long studentId, Long courseClassId);

    List<StudentCourseClass> findByStudentIdAndSemesterIdAndStatusIn(Long studentId, Long semesterId, Set<StudentCourseClassStatus> status);

    @Query(value = """
                SELECT COALESCE(SUM(s.credits), 0)
                FROM student_course_classes scc
                JOIN course_classes cc ON scc.course_class_id = cc.id
                JOIN subjects s ON cc.subject_id = s.id
                WHERE scc.student_id = :studentId
                    AND scc.status IN ('PENDING', 'ENROLLED')
                    AND cc.semester_id = :semesterId
            """, nativeQuery = true)
    Integer findCreditsRegistered(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    @Query(value = """
                SELECT COUNT(*)
                FROM student_course_classes scc
                WHERE scc.course_class_id = :courseClassId
                    AND scc.status IN ('PENDING', 'ENROLLED')
            """, nativeQuery = true)
    Integer countEnroll(@Param("courseClassId") Long courseClassId);

    @Query(value = """
        SELECT EXISTS (
            SELECT 1
            FROM student_course_classes scc
            JOIN course_classes cc ON scc.course_class_id = cc.id
            WHERE scc.student_id = :studentId
            AND cc.subject_id = :subjectId
            AND cc.semester_id = :semesterId
            AND scc.status IN ('PENDING', 'ENROLLED')
        )
    """, nativeQuery = true)
    boolean existsByStudentIdAndSubjectIdAndSemesterIdAndStatusIn(
        @Param("studentId") Long studentId,
        @Param("subjectId") Long subjectId,
        @Param("semesterId") Long semesterId,
        @Param("status") Set<StudentCourseClassStatus> status
    );
}
