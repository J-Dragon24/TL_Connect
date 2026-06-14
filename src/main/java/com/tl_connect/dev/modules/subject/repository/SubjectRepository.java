package com.tl_connect.dev.modules.subject.repository;

import java.util.List;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.subject.entity.Subject;
import com.tl_connect.dev.modules.subject.projection.PrerequisiteRow;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query(value = """
            SELECT * 
            FROM subjects
            WHERE is_active = true
            ORDER BY subject_name ASC
            """,
            countQuery = """
                SELECT COUNT(*) FROM subjects WHERE is_active = true
                """,
            nativeQuery = true)
    Page<Subject> findAllSubjects(Pageable pageable);

    List<Subject> findBySubjectCodeIn(Set<String> subjectCodes);

    List<Subject> findByIdIn(List<Long> ids);

    Optional<Subject> findBySubjectCode(String subjectCode);
    
    @Query(value ="""
            SELECT 
                s.id as subjectId,
                s.subject_code as subjectCode,
                spg.id as groupId,
                spg.min_subjects_required as minSubjectsRequired,
                spgi.prerequisite_subject_id as prerequisiteSubjectId
            FROM subjects s
            LEFT JOIN subject_prerequisite_groups spg ON spg.subject_id = s.id
            LEFT JOIN subject_prerequisite_group_items spgi ON spgi.group_id = spg.id
            WHERE s.is_active = TRUE
            ORDER BY s.id, spg.id
            """, nativeQuery = true)
    List<PrerequisiteRow> findAllPrerequisiteRows();

    boolean existsBySubjectCode(String subjectCode);

    boolean existsById(Long id);

    @Query("SELECT COUNT(s.id) FROM Subject s WHERE s.id IN :ids")
    long countByIdIn(@Param("ids") List<Long> ids);
}
