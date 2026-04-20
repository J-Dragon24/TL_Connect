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
public class AcademicResultByStudyProgramDTO {
    private String majorName;
    private String studyProgramCode;
    private String studyProgramName;
    private List<SemesterResultDTO> semesterResults;
}
