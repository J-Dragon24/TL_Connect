package com.tl_connect.dev.modules.enroll.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.dto.StudentEnrollmentProfile;
import com.tl_connect.dev.modules.subject.entity.SubjectEnrollmentCondition;
import com.tl_connect.dev.modules.subject.repository.SubjectEnrollmentConditionRepository;
import com.tl_connect.dev.shared.common.exception.EnrollmentConditionNotMetException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentConditionService {

    private final SubjectEnrollmentConditionRepository conditionRepository;

    public void check(Long subjectId, StudentEnrollmentProfile profile) {
        List<SubjectEnrollmentCondition> conditions = conditionRepository.findBySubjectId(subjectId);

        List<String> violations = new ArrayList<>();

        for (SubjectEnrollmentCondition condition : conditions) {
            boolean met = switch (condition.getConditionType()) {
                case GPA -> compare(
                    profile.getCumulativeGpa(), 
                    condition.getConditionValue(), 
                    condition.getConditionOperator()
                );
                case TOTAL_CREDITS -> compare(
                    BigDecimal.valueOf(profile.getTotalCredits()),
                    condition.getConditionValue(),
                    condition.getConditionOperator()
                );
                default -> true;
            };

            if (!met) {
                violations.add(condition.getDescription());
            }
        }

        if (!violations.isEmpty()) {
            throw new EnrollmentConditionNotMetException("You haven't met the prerequisite conditions for this course.", violations);
        }
    }

    private boolean compare(BigDecimal actual, BigDecimal required, String operator) {
        int cmp = actual.compareTo(required);
        return switch (operator) {
            case ">=" -> cmp >= 0;
            case ">"  -> cmp > 0;
            case "="  -> cmp == 0;
            case "<=" -> cmp <= 0;
            default   -> false;
        };
    }
}