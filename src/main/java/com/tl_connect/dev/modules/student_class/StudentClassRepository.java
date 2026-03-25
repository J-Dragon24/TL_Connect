package com.tl_connect.dev.modules.student_class;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.student_class.entity.StudentClass;
import com.tl_connect.dev.modules.student_class.projection.ClassHeaderView;
import com.tl_connect.dev.modules.student_class.projection.StudentClassRow;
import com.tl_connect.dev.modules.student_class.projection.StudentInClassRow;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Long> {

    @Query(value = """
            SELECT
                s.student_code AS studentCode,
                s.full_name AS fullName,
                s.gender AS gender
            FROM students s
            JOIN student_classes c ON s.student_class_id = c.id
            WHERE c.id = :classId
            """, nativeQuery = true)
    List<StudentInClassRow> findStudentsByClassId(@Param("classId") Long classId);

    List<StudentClass> findByClassCodeIn(Set<String> classCodes);

    Optional<StudentClass> findByClassCode(String classCode);

    boolean existsByClassCode(String classCode);

    @Query(value= """
        SELECT sc.id as id,
            sc.class_code as classCode,
            m.major_name as majorName,
            sc.start_year as startYear,
            COUNT(s.id) as studentCount
        FROM student_classes sc
        LEFT JOIN majors m ON sc.major_id = m.id
        LEFT JOIN students s ON s.student_class_id = sc.id
        GROUP BY sc.id, sc.class_code, m.major_name, sc.start_year
    """, nativeQuery = true)
    Page<StudentClassRow> getAllWithStudentCount(Pageable pageable);

    @Query(value = """
            SELECT
                c.id AS classId,
                c.class_code AS classCode,
                m.major_name AS major,
                c.start_year AS startYear,
                l.lecturer_code AS lecturerCode,
                l.full_name AS academicAdvisor,
                l.phone_number AS phoneNumber,
                l.email AS email
            FROM student_classes c
            LEFT JOIN majors m ON c.major_id = m.id
            LEFT JOIN academic_advisors aa ON c.id = aa.student_class_id
            LEFT JOIN lecturers l ON aa.lecturer_id = l.id
            WHERE c.id = :classId
            """, nativeQuery = true)
    Optional<ClassHeaderView> findClassHeaderByClassId(@Param("classId") Long classId);
}
