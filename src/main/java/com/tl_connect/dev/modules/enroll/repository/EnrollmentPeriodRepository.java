package com.tl_connect.dev.modules.enroll.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tl_connect.dev.modules.enroll.entity.EnrollmentPeriod;

public interface EnrollmentPeriodRepository extends JpaRepository<EnrollmentPeriod, Long> {
    Optional<EnrollmentPeriod> findBySemesterId(Long semesterId);
}
