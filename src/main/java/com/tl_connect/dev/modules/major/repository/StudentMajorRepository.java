package com.tl_connect.dev.modules.major.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.major.entity.StudentMajor;

@Repository
public interface StudentMajorRepository extends JpaRepository<StudentMajor, Long> {
    @Query(value = """
            SELECT sm.*
            FROM student_majors sm
            WHERE sm.student_id = :studentId AND sm.is_primary = true
            """, nativeQuery = true)
    Optional<StudentMajor> findPrimaryByStudentId(@Param("studentId") Long studentId);
}
