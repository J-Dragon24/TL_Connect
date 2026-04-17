package com.tl_connect.dev.modules.course_class;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.course_class.projection.CourseClassBasicInfoRow;
import com.tl_connect.dev.modules.course_class.projection.CourseClassRow;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseClassRepository extends JpaRepository<CourseClass, Long> {

    boolean existsByClassCode(String classCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cc FROM CourseClass cc WHERE cc.id = :id")
    Optional<CourseClass> findByIdForUpdate(@Param("id") Long id);


        @Query(value = """
            SELECT 
                cc.id as id,
                l.lecturer_code as lecturerCode,
                l.full_name as lecturerName,
                s.subject_code as subjectCode,
                s.subject_name as subjectName,
                sem.semester_code as semesterCode,
                sem.semester_name as semesterName,
                sem.academic_years as academicYears,
                sem.semester_number as semesterNumber,
                sem.start_date as startDate,
                sem.end_date as endDate,
                cc.class_code as classCode,
                cc.class_name as className,
                cc.capacity as capacity,
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
            cc.is_active as isActive
        FROM course_classes cc
        JOIN lecturers l ON cc.lecturer_id = l.id
        JOIN subjects s ON cc.subject_id = s.id
        JOIN semesters sem ON cc.semester_id = sem.id
        JOIN faculties f ON s.faculty_id = f.id
        WHERE (:facultyCode IS NULL OR :facultyCode = '' OR f.faculty_code = :facultyCode)
        """,
        countQuery = """
                SELECT COUNT(cc.id) FROM course_classes cc
                JOIN lecturers l ON cc.lecturer_id = l.id
                JOIN subjects s ON cc.subject_id = s.id
                JOIN semesters sem ON cc.semester_id = sem.id
                JOIN faculties f ON s.faculty_id = f.id
                WHERE (:facultyCode IS NULL OR :facultyCode = '' OR f.faculty_code = :facultyCode)
                """,
    nativeQuery = true)
    Page<CourseClassBasicInfoRow> findAllCourseClass(Pageable pageable, @Param("facultyCode") String facultyCode);

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
}
