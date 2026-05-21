package com.tl_connect.dev.modules.subject.service.interfaces;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.subject.dto.CreateSubjectDTO;
import com.tl_connect.dev.modules.subject.dto.SubjectDTO;
import com.tl_connect.dev.modules.subject.dto.UpdateSubjectDTO;
import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.projection.PrerequisiteRow;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface SubjectService {
    PagedResponse<Subject> getAllSubjects(Pageable pageable);

    SubjectDTO getSubjectById(Long id);

    Long create(CreateSubjectDTO dto);

    void update(Long id, UpdateSubjectDTO dto);

    void delete(Long id);

    List<Subject> findBySubjectCodeIn(Set<String> subjectCodes);

    Subject findById(Long id);

    List<PrerequisiteRow> findAllPrerequisiteRows();
}
