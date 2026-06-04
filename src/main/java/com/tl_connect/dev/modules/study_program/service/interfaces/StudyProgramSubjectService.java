package com.tl_connect.dev.modules.study_program.service.interfaces;

import com.tl_connect.dev.modules.study_program.dto.CreateStudyProgramSubDTO;
import com.tl_connect.dev.modules.study_program.dto.UpdateStudyProgramSubDTO;

public interface StudyProgramSubjectService {

    Long createStudyProgramSubject(Long studyProgramId, CreateStudyProgramSubDTO createStudyProgramSubjectDTO);

    void updateStudyProgramSubject(Long studyProgramSubjectId, UpdateStudyProgramSubDTO updateStudyProgramSubjectDTO);

    void deleteStudyProgramSubject(Long studyProgramSubjectId);
}
