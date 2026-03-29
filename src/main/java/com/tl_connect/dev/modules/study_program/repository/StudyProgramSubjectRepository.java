package com.tl_connect.dev.modules.study_program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.study_program.entity.StudyProgramSubject;

@Repository
public interface StudyProgramSubjectRepository extends JpaRepository<StudyProgramSubject, Long> {
    boolean existsByStudyProgramIdAndSubjectId(Long studyProgramId, Long subjectId);
}
