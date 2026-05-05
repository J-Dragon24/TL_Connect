package com.tl_connect.dev.modules.enroll.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;
import com.tl_connect.dev.modules.enroll.projection.StudentCourseClassRow;
import com.tl_connect.dev.shared.common.enums.StudentCourseClassStatus;
import com.tl_connect.dev.shared.datastructure.intervaltree.ScheduleInterval;


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

    List<StudentCourseClass> findByStudentIdAndSemesterIdAndStatus(Long studentId, Long semesterId, StudentCourseClassStatus status);

    @Query(value = """
        SELECT 
            cs.id as classScheduleId,
            cs.course_class_id as courseClassId,
            cc.class_code as classCode,
            cs.day_of_week as dayOfWeek,
            cs.start_period as startPeriod,
            cs.end_period as endPeriod
        FROM student_course_classes scc
        JOIN course_classes cc ON cc.id = scc.course_class_id
        JOIN class_schedules cs ON cs.course_class_id = cc.id
        WHERE scc.student_id = :studentId
        AND scc.semester_id = :semesterId
        AND scc.status IN ('PENDING', 'ENROLLED')
        """, nativeQuery = true)
    List<ScheduleInterval> findCurrentSchedule(
        @Param("studentId") Long studentId,
        @Param("semesterId") Long semesterId
    );

    @Modifying
    @Query("UPDATE StudentCourseClass scc SET scc.status = :toStatus WHERE scc.semesterId = :semesterId AND scc.status = :fromStatus")
    void updateStatusBySemesterId(
        @Param("semesterId") Long semesterId,
        @Param("fromStatus") StudentCourseClassStatus fromStatus,
        @Param("toStatus") StudentCourseClassStatus toStatus
    );

    @Query(value = """
            SELECT 
                scc.id as id,
                s.student_code as studentCode,
                s.full_name as studentName,
                cc.class_code as classCode,
                cc.class_name as className,
                su.subject_code as subjectCode,
                su.subject_name as subjectName,
                sm.semester_code as semesterCode,
                sm.semester_name as semesterName,
                scc.status as status,
                scc.is_retake as isRetake,
                scc.created_at as createdAt,
                scc.updated_at as updatedAt
                FROM student_course_classes scc
                JOIN course_classes cc ON cc.id = scc.course_class_id
                JOIN students s ON s.id = scc.student_id
                JOIN subjects su ON su.id = cc.subject_id
                JOIN semesters sm ON sm.id = cc.semester_id
                JOIN student_majors smj ON smj.student_id = s.id
            WHERE 1=1
                AND (:majorId IS NULL OR smj.major_id = :majorId)
                AND (:semesterId IS NULL OR sm.id = :semesterId)
                AND (:studentId IS NULL OR s.id = :studentId)
            ORDER BY s.student_code, s.full_name, scc.created_at ASC
        """,
        countQuery = """
            SELECT 
                COUNT(*)
            FROM student_course_classes scc
            JOIN course_classes cc ON cc.id = scc.course_class_id
            JOIN students s ON s.id = scc.student_id
            JOIN subjects su ON su.id = cc.subject_id
            JOIN semesters sm ON sm.id = cc.semester_id
            JOIN student_majors smj ON smj.student_id = s.id
        WHERE 1=1
            AND (:majorId IS NULL OR smj.major_id = :majorId)
            AND (:semesterId IS NULL OR sm.id = :semesterId)
            AND (:studentId IS NULL OR s.id = :studentId)
        """,
        nativeQuery = true)
    Page<StudentCourseClassRow> getAllStudentEnrollment(
        @Param("semesterId") Long semesterId,
        @Param("studentId") Long studentId,
        @Param("majorId") Long majorId,
        Pageable pageable
    );
}
