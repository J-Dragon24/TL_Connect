package com.tl_connect.dev.modules.exam.service.interfaces;

import org.springframework.data.domain.Pageable;

import com.tl_connect.dev.modules.exam.dto.ExamScheduleBasicInfoDTO;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.CreateExamScheduleDTO;
import com.tl_connect.dev.modules.exam.dto.UpdateExamScheduleDTO;
import com.tl_connect.dev.shared.common.dto.PagedResponse;

public interface ExamService {
    ExamScheduleDTO getExamSchedule(Long studentId, String semesterCode);

    PagedResponse<ExamScheduleBasicInfoDTO> getExamSchedule(Long semesterId, Pageable pageable, Long facultyId);

    Long createExamSchedule(CreateExamScheduleDTO createExamScheduleDTO);

    void updateExamSchedule(Long id, UpdateExamScheduleDTO updateExamScheduleDTO);

    void deleteExamSchedule(Long id);
}
