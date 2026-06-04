package com.tl_connect.dev.modules.tuition.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tl_connect.dev.modules.tuition.entity.TuitionFeeConfig;

public interface TuitionFeeConfigRepository extends JpaRepository<TuitionFeeConfig, Long> {

    @Query(
        value = "SELECT * FROM tuition_fee_configs ORDER BY academic_year DESC, cohort ASC",
        countQuery = "SELECT COUNT(*) FROM tuition_fee_configs",
        nativeQuery = true
    )
    Page<TuitionFeeConfig> findAllConfigs(Pageable pageable);

}
