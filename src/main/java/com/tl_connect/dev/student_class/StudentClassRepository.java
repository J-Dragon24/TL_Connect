package com.tl_connect.dev.student_class;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.student_class.entity.StudentClass;
import com.tl_connect.dev.student_class.projection.StudentInClassRow;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Long> {

    @Query("""
            SELECT
                s.studentCode AS studentCode,
                s.fullName AS fullName,
                s.gender AS gender
            FROM Student s
            JOIN StudentClass c ON s.studentClassId = c.id
            WHERE c.id = :classId
            """)
    List<StudentInClassRow> findStudentsByClassId(@Param("classId") Long classId);

}
