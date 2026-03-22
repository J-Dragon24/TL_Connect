package com.tl_connect.dev.modules.student.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.student.entity.AcademicInfo;

@Repository
public interface AcademicInfoRepository extends JpaRepository<AcademicInfo, Long> {
    Optional<AcademicInfo> findByStudentMajorId(Long studentMajorId);
}
