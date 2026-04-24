package com.tl_connect.dev.modules.subject.dto;

import java.math.BigDecimal;

import com.tl_connect.dev.core.common.enums.SubjectConditionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentConditionDTO {
    private Long id;
    private SubjectConditionType conditionType;
    private BigDecimal conditionValue;
    private String conditionOperator;
    private String description;
}
