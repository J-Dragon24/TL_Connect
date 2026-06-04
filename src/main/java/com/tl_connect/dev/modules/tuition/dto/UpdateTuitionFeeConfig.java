package com.tl_connect.dev.modules.tuition.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTuitionFeeConfig {
    private BigDecimal basePricePerCredit;

    private String academicYear;

    private Integer cohort;
}
