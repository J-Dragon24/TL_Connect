package com.tl_connect.dev.modules.tuition.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTuitionFeeConfig {
    @NotNull(message = "Base price per credit is required")
    private BigDecimal basePricePerCredit;
    @NotNull(message = "Academic year is required")
    private String academicYear;
    @NotNull(message = "Cohort is required")
    private Integer cohort;
}
