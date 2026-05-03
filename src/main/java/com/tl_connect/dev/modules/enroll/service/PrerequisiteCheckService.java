package com.tl_connect.dev.modules.enroll.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.dto.dag.PrerequisiteGroup;
import com.tl_connect.dev.modules.enroll.dto.dag.SubjectNode;
import com.tl_connect.dev.shared.common.exception.EnrollmentConditionNotMetException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrerequisiteCheckService {

    private final PrerequisiteDAGService dagService;

    public void check(Long subjectId, Set<Long> passedSubjectIds) {
        Map<Long, SubjectNode> dag = dagService.getDAG();
        checkDirectPrerequisites(subjectId, dag, passedSubjectIds);
    }

    private void checkDirectPrerequisites(
            Long subjectId,
            Map<Long, SubjectNode> dag,
            Set<Long> passedSubjectIds
        ) {
        SubjectNode node = dag.get(subjectId);
        if (node == null || node.getGroups().isEmpty()) {
            return;
        }

        List<EnrollmentConditionNotMetException.MissingGroup> missingGroups = new ArrayList<>();

        for (PrerequisiteGroup group : node.getGroups()) {
            int passedCount = 0;
            List<Long> missingInGroup = new ArrayList<>();

            for (Long prereqId : group.getPrereqs()) {
                if (passedSubjectIds.contains(prereqId)) {
                    passedCount++;
                } else {
                    missingInGroup.add(prereqId);
                }
            }

            if (passedCount < group.getMinRequired()) {
                List<String> missingSubjectCodes = new ArrayList<>();
                for (Long missingId : missingInGroup) {
                    String code = dag.get(missingId).getCode();
                    missingSubjectCodes.add(code);
                }
                missingGroups.add(EnrollmentConditionNotMetException.MissingGroup.builder()
                    .groupId(group.getGroupId())
                    .needMore(group.getMinRequired() - passedCount)
                    .missingSubjectCodes(missingSubjectCodes)
                    .build());
            }
        }

        if (!missingGroups.isEmpty()) {
            throw new EnrollmentConditionNotMetException("You have not met the prerequisite condition", missingGroups);
        }
    }
}