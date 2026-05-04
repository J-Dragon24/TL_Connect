package com.tl_connect.dev.modules.enroll.dto;

import java.util.Set;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentEnrollmentProfile {
    private Set<Long> passedSubjectIds;
    private Set<Long> failedSubjectIds;
    private BigDecimal cumulativeGpa;
    private int totalCredits;
}
