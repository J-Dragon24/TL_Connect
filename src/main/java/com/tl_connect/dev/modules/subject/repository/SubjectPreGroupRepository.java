package com.tl_connect.dev.modules.subject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.subject.entity.SubjectPrerequisiteGroup;

@Repository
public interface SubjectPreGroupRepository extends JpaRepository<SubjectPrerequisiteGroup, Long> {
    List<SubjectPrerequisiteGroup> findBySubjectIdIn(List<Long> subjectIds);

    List<SubjectPrerequisiteGroup> findBySubjectId(Long subjectId);
}
