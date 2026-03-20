package com.tl_connect.dev.modules.subject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.subject.entity.SubjectEnrollmentCondition;

@Repository
public interface SubjectEnrollmentConditionRepository extends JpaRepository<SubjectEnrollmentCondition, Long> {
    List<SubjectEnrollmentCondition> findBySubjectId(Long subjectId);
}
