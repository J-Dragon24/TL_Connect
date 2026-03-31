package com.tl_connect.dev.modules.academic_result.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterSummaryDTO {
    private Integer creditsRegistered;
    private Integer creditsPassed;
    private BigDecimal semesterGpa;
    private Integer conductScore;
}
