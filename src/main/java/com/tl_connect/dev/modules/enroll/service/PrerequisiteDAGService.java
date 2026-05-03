package com.tl_connect.dev.modules.enroll.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.enroll.dto.dag.PrerequisiteGroup;
import com.tl_connect.dev.modules.subject.projection.PrerequisiteRow;
import com.tl_connect.dev.modules.enroll.dto.dag.SubjectNode;
import com.tl_connect.dev.modules.subject.repository.SubjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrerequisiteDAGService {

    private final SubjectRepository subjectRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String DAG_CACHE_KEY = "prereq:dag";

    public Map<Long, SubjectNode> getDAG() {
        // Thử lấy từ Redis trước
        Object cached = redisTemplate.opsForValue().get(DAG_CACHE_KEY);
        if (cached != null) {
            return (Map<Long, SubjectNode>) cached;
        }

        List<PrerequisiteRow> rows = subjectRepository.findAllPrerequisiteRows();
        Map<Long, SubjectNode> dag = buildDAG(rows);

        // Lưu vào Redis, không TTL vì chỉ invalidate khi admin update
        redisTemplate.opsForValue().set(DAG_CACHE_KEY, dag);
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
