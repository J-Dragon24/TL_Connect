package com.tl_connect.dev.modules.subject.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.tl_connect.dev.core.common.enums.ConditionEnrollmentType;
import com.tl_connect.dev.core.common.exception.InvalidInputException;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subject_enrollment_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectEnrollmentCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ConditionEnrollmentType conditionType;

    @Column(name = "condition_value", nullable = false)
    private BigDecimal conditionValue;

    @Column(name = "condition_operator")
    private String conditionOperator;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static SubjectEnrollmentCondition create(Long subjectId, ConditionEnrollmentType conditionType, BigDecimal conditionValue, String conditionOperator, String description) {
        if(conditionValue.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidInputException("Invalid condition value");
        }
        if(conditionType == null){
            throw new InvalidInputException("Invalid condition type");
        }
        if(conditionOperator == null || conditionOperator.equals("")){
            throw new InvalidInputException("Invalid condition operator");
        }
        if(subjectId == null){
            throw new InvalidInputException("Invalid subject id");
        }
        if(description == null || description.equals("")){
            throw new InvalidInputException("Invalid description");
        }
        SubjectEnrollmentCondition subjectEnrollmentCondition = new SubjectEnrollmentCondition();
        subjectEnrollmentCondition.setSubjectId(subjectId);
        subjectEnrollmentCondition.setConditionType(conditionType);
        subjectEnrollmentCondition.setConditionValue(conditionValue);
        subjectEnrollmentCondition.setConditionOperator(conditionOperator);
        subjectEnrollmentCondition.setDescription(description);
        return subjectEnrollmentCondition;
    }
}
