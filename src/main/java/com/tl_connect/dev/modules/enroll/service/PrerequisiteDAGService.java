package com.tl_connect.dev.modules.enroll.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tl_connect.dev.modules.enroll.dto.dag.PrerequisiteGroup;
import com.tl_connect.dev.modules.subject.projection.PrerequisiteRow;
import com.tl_connect.dev.modules.enroll.dto.dag.SubjectNode;
import com.tl_connect.dev.modules.subject.service.interfaces.SubjectService;
import com.tl_connect.dev.shared.ultility.CacheHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrerequisiteDAGService {

    private final SubjectService subjectService;
    private final CacheHelper cacheHelperHelper;

    private static final String DAG_CACHE_KEY = "prereq:dag";

    public Map<Long, SubjectNode> getDAG() {
        return cacheHelperHelper.getOrSet(DAG_CACHE_KEY, new TypeReference<Map<Long, SubjectNode>>() {}, () -> {
            List<PrerequisiteRow> rows = subjectService.findAllPrerequisiteRows();
            return buildDAG(rows);
        });
    }

    public void invalidateDAG() {
        cacheHelperHelper.evict(DAG_CACHE_KEY);
    }

    private Map<Long, SubjectNode> buildDAG(List<PrerequisiteRow> rows) {
        Map<Long, SubjectNode> dag = new HashMap<>();

        for (PrerequisiteRow row : rows) {
            // Tạo node nếu chưa có
            dag.computeIfAbsent(row.getSubjectId(), id -> {
                SubjectNode node = new SubjectNode();
                node.setCode(row.getSubjectCode());
                return node;
            });

            if (row.getGroupId() == null) continue; // môn không có prereq

            SubjectNode node = dag.get(row.getSubjectId());

            // Tìm hoặc tạo group
            PrerequisiteGroup group = node.getGroups().stream()
                .filter(g -> g.getGroupId().equals(row.getGroupId()))
                .findFirst()
                .orElseGet(() -> {
                    PrerequisiteGroup newGroup = new PrerequisiteGroup();
                    newGroup.setGroupId(row.getGroupId());
                    newGroup.setMinRequired(row.getMinSubjectsRequired());
                    node.getGroups().add(newGroup);
                    return newGroup;
                });

            if (row.getPrerequisiteSubjectId() != null) {
                group.getPrereqs().add(row.getPrerequisiteSubjectId());
            }
        }

        return dag;
    }
}
