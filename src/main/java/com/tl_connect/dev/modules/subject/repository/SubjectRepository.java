package com.tl_connect.dev.modules.subject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.projection.SubjectPrerequisiteConditionRow;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @Query(value ="""
            SELECT g.id AS id,
                g.min_subjects_required AS minSubjectsRequired,
                COUNT(ssr.subject_id) AS passedCount,
                COUNT(gi.prerequisite_subject_id) AS total
            FROM subject_prerequisite_groups g
            JOIN subject_prerequisite_group_items gi 
                ON gi.group_id = g.id
            LEFT JOIN student_subject_results ssr 
                ON ssr.subject_id = gi.prerequisite_subject_id
                AND ssr.student_id = :studentId
                AND ssr.is_pass = TRUE
            WHERE g.subject_id = :subjectId
            GROUP BY g.id, g.min_subjects_required;
            """, nativeQuery = true)
    List<SubjectPrerequisiteConditionRow> findSubjectPrerequisiteCondition(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);
}
