package com.tl_connect.dev.modules.major.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.major.entity.StudentMajor;

@Repository
public interface StudentMajorRepository extends JpaRepository<StudentMajor, Long> {
}
