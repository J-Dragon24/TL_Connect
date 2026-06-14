package com.tl_connect.dev.modules.course_class;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.course_class.projection.CourseClassBasicInfoRow;
import com.tl_connect.dev.modules.course_class.projection.CourseClassRow;
import com.tl_connect.dev.modules.enroll.projection.CourseClassForEnrollRow;
import com.tl_connect.dev.modules.enroll.projection.DetailsForCheckEnrollRow;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseClassRepository extends JpaRepository<CourseClass, Long> {

    boolean existsByClassCode(String classCode);

    @Query(value = """
            SELECT
            cs.id as classScheduleId,
            s.credits as credits,
            cs.day_of_week as dayOfWeek,
            cs.start_period as startPeriod,
            cs.end_period as endPeriod
            FROM class_schedules cs
            JOIN course_classes cc ON cc.id = cs.course_class_id
            JOIN subjects s ON s.id = cc.subject_id
            WHERE cs.course_class_id = :id
            """,nativeQuery = true)
    List<DetailsForCheckEnrollRow> findDetailForEnrollmentById(@Param("id") Long id);


    @Query(value = """
            SELECT 
                cc.id as id,
                l.lecturer_code as lecturerCode,
                l.full_name as lecturerName,
                s.subject_code as subjectCode,
                s.subject_name as subjectName,
                sem.id as semesterId,
                sem.semester_code as semesterCode,
                sem.semester_name as semesterName,
                sem.academic_years as academicYears,
                sem.semester_number as semesterNumber,
                sem.start_date as startDate,
                sem.end_date as endDate,
                cc.class_code as classCode,
                cc.class_name as className,
                cc.capacity as capacity,
                cc.enrolled_count as enrolledCount,
                cc.is_active as isActive
            FROM course_classes cc
            JOIN lecturers l ON cc.lecturer_id = l.id
            JOIN subjects s ON cc.subject_id = s.id
            JOIN semesters sem ON cc.semester_id = sem.id
            WHERE cc.id = :id
            """,nativeQuery = true)
    Optional<CourseClassRow> findDetailById(@Param("id") Long id);


    @Query(value = """
        SELECT 
            cc.id as id,
            l.lecturer_code as lecturerCode,
            s.subject_code as subjectCode,
            sem.semester_code as semesterCode,
            cc.class_code as classCode,
            cc.class_name as className,
            cc.capacity as capacity,
            cc.enrolled_count as enrolledCount,
            cc.is_active as isActive
        FROM course_classes cc
        JOIN lecturers l ON cc.lecturer_id = l.id
        JOIN subjects s ON cc.subject_id = s.id
        JOIN semesters sem ON cc.semester_id = sem.id
        JOIN faculties f ON s.faculty_id = f.id
        WHERE (:facultyCode IS NULL OR :facultyCode = '' OR f.faculty_code = :facultyCode)
        AND (:semesterCode IS NULL OR :semesterCode = '' OR sem.semester_code = :semesterCode)
        AND cc.is_active = true
        ORDER BY cc.class_code ASC, cc.class_name ASC
        """,
        countQuery = """
                SELECT COUNT(cc.id) FROM course_classes cc
                JOIN lecturers l ON cc.lecturer_id = l.id
                JOIN subjects s ON cc.subject_id = s.id
                JOIN semesters sem ON cc.semester_id = sem.id
                JOIN faculties f ON s.faculty_id = f.id
                WHERE (:facultyCode IS NULL OR f.faculty_code = :facultyCode)
                AND (:semesterCode IS NULL OR :semesterCode = '' OR sem.semester_code = :semesterCode)
                """,
    nativeQuery = true)
    Page<CourseClassBasicInfoRow> findAllCourseClass(Pageable pageable, @Param("facultyCode") String facultyCode, @Param("semesterCode") String semesterCode);

    @Query(value = """
            SELECT 
                cc.id as id
            FROM course_classes cc
            JOIN student_course_classes scc ON cc.id = scc.course_class_id
            JOIN semesters sem ON cc.semester_id = sem.id
            WHERE scc.student_id = :studentId
            AND :now BETWEEN sem.start_date AND sem.end_date
            """, nativeQuery = true)
    List<Long> findIdsByStudentIdAndSemesterId(@Param("studentId") Long studentId, @Param("now") LocalDate now);

    @Query(value = """
            SELECT 
                cc.id as id,
                l.lecturer_code as lecturerCode,
                l.full_name as lecturerName,
                cc.class_code as classCode,
                cc.class_name as className,
                cc.capacity as capacity,
                cc.enrolled_count as enrolledCount,
                cs.day_of_week AS dayOfWeek,
                cs.start_period AS startPeriod,
                cs.end_period AS endPeriod,
                cs.start_time AS startTime,
                cs.end_time AS endTime,
                cs.room AS room
            FROM course_classes cc
            LEFT JOIN lecturers l ON cc.lecturer_id = l.id
            LEFT JOIN class_schedules cs ON cs.course_class_id = cc.id
            WHERE cc.subject_id = :subjectId
            AND cc.semester_id = :semesterId
            AND cc.is_active = true
            """, nativeQuery = true)
    List<CourseClassForEnrollRow> findCourseClassForEnrollment(@Param("subjectId") Long subjectId, @Param("semesterId") Long semesterId);

    @Modifying
    @Query(value = """
            UPDATE course_classes cc
            SET enrolled_count = enrolled_count + 1
            WHERE cc.id = :id
            AND cc.enrolled_count < cc.capacity
            """, nativeQuery = true)
    int incrementEnrolledCount(@Param("id") Long id);

    @Modifying
    @Query(value = """
            UPDATE course_classes cc
            SET enrolled_count = enrolled_count - 1
            WHERE cc.id = :id
            AND cc.enrolled_count > 0
            """, nativeQuery = true)
    int decrementEnrolledCount(@Param("id") Long id);
}
