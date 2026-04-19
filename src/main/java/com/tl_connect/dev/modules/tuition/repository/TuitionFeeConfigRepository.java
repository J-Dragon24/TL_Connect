package com.tl_connect.dev.modules.tuition.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tl_connect.dev.modules.tuition.entity.TuitionFeeConfig;

public interface TuitionFeeConfigRepository extends JpaRepository<TuitionFeeConfig, Long> {

}
