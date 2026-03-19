package com.tl_connect.dev.modules.subject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.subject.entity.SubjectPrerequisiteGroupItem;

@Repository
public interface SubjectPreGroupItemRepository extends JpaRepository<SubjectPrerequisiteGroupItem, Long> {
    List<SubjectPrerequisiteGroupItem> findByGroupIdIn(List<Long> groupIds);
}
