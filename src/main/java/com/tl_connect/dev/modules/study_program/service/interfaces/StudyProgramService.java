package com.tl_connect.dev.modules.study_program.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.enroll.projection.SubjectForEnrollRow;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramAdmDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramDTO;
import com.tl_connect.dev.modules.study_program.dto.StudyProgramListItemDTO;
import com.tl_connect.dev.modules.study_program.entity.StudyProgram;
import com.tl_connect.dev.modules.study_program.projection.StudyProgramHeaderView;
import com.tl_connect.dev.shared.common.dto.PagedResponse;
import com.tl_connect.dev.shared.common.enums.TrainingType;

import java.util.List;

public interface StudyProgramService {

    PagedResponse<StudyProgramAdmDTO> getAllStudyProgram(Pageable pageable, Integer startYear, String facultyCode);

    StudyProgramDTO getDetailedAdminStudyProgram(Long studyProgramId);

    List<StudyProgramListItemDTO> getBasicInfoStudyProgram(Long studentId);

    StudyProgramHeaderView findByStudyProgramHeader(String studyProgramCode, Long studentId);
    
    StudyProgramDTO getDetailedStudyProgram(Long studyProgramId, StudyProgramHeaderView headerView);

    StudyProgramHeaderView findByStudyProgramCodeAndStudentId(String studyProgramCode, Long studentId);

    List<SubjectForEnrollRow> findSubjectsByStudyProgramId(Long studyProgramId);

    StudyProgram findByMajorIdAndTrainingTypeAndStartYear(Long majorId, TrainingType trainingType, Integer startYear);

    StudyProgram findById(Long id);
}
