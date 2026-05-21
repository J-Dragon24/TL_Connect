package com.tl_connect.dev.modules.enroll.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tl_connect.dev.modules.enroll.dto.dag.PrerequisiteGroup;
import com.tl_connect.dev.modules.subject.projection.PrerequisiteRow;
import com.tl_connect.dev.modules.enroll.dto.dag.SubjectNode;
import com.tl_connect.dev.modules.subject.service.interfaces.SubjectService;
import com.tl_connect.dev.shared.common.ultility.JsonHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrerequisiteDAGService {

    private final SubjectService subjectService;
    private final StringRedisTemplate redisTemplate;
    private final JsonHelper jsonHelper;

    private static final String DAG_CACHE_KEY = "prereq:dag";

    public Map<Long, SubjectNode> getDAG() {
        String cached = (String) redisTemplate.opsForValue().get(DAG_CACHE_KEY);
        if (cached != null) {
            return jsonHelper.fromJson(cached, new TypeReference<Map<Long, SubjectNode>>() {});
        }

        List<PrerequisiteRow> rows = subjectService.findAllPrerequisiteRows();
        Map<Long, SubjectNode> dag = buildDAG(rows);

        try{
            redisTemplate.opsForValue().set(DAG_CACHE_KEY, jsonHelper.toJson(dag));
        }catch (Exception e) {
            log.warn("Error when caching DAG", e);
        }
        return dag;
    }

    public void invalidateDAG() {
        redisTemplate.delete(DAG_CACHE_KEY);
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
