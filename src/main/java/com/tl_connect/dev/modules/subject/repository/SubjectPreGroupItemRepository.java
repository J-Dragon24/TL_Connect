package com.tl_connect.dev.modules.subject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.subject.entity.SubjectPrerequisiteGroupItem;
import com.tl_connect.dev.modules.subject.projection.SubjectPrerequisiteGroupItemRow;

@Repository
public interface SubjectPreGroupItemRepository extends JpaRepository<SubjectPrerequisiteGroupItem, Long> {


    @Query(value = """
            SELECT 
                gi.group_id as groupId,
                gi.prerequisite_subject_id as prerequisiteSubjectId,
                s.subject_code as subjectCode,
                s.subject_name as subjectName
            FROM subject_prerequisite_group_items gi
            JOIN subjects s ON gi.prerequisite_subject_id = s.id
            WHERE gi.group_id IN :groupIds
            """, nativeQuery = true)
    List<SubjectPrerequisiteGroupItemRow> findByGroupIdIn(@Param("groupIds") List<Long> groupIds);
}
