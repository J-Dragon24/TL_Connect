package com.tl_connect.dev.modules.enroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.enroll.entity.StudentCourseClass;

@Repository
public interface StudentCourseClassRepository extends JpaRepository<StudentCourseClass, Long> {
    
}
