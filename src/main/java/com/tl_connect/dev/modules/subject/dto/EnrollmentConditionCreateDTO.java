package com.tl_connect.dev.modules.subject.dto;

import java.math.BigDecimal;

import com.tl_connect.dev.shared.common.enums.SubjectConditionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentConditionCreateDTO {
    private SubjectConditionType conditionType;
    private BigDecimal conditionValue;
    private String conditionOperator;
    private String description;
}
