package com.tl_connect.dev.modules.student_class;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.student_class.entity.StudentClass;
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
}
