package com.tl_connect.dev.modules.academic_result.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicResultAdmDTO {
    private Long studentId;
    private String studentCode;
    private String studentName;
    private Integer startYear;
    private List<AcademicResultByStudyProgramDTO> studyPrograms;
}
