package com.tl_connect.dev.modules.study_program.service.interfaces;

import com.tl_connect.dev.modules.study_program.dto.CreateStudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.UpdateStudyProgramDTO;

public interface StudyProgramModifyService {

    Long createStudyProgram(CreateStudyProgramDTO createStudyProgramDTO);

    void updateStudyProgram(Long id, UpdateStudyProgramDTO updateStudyProgramDTO);

    void deleteStudyProgram(Long id);
}
