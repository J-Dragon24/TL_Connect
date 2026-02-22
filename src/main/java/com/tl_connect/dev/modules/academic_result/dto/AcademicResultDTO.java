package com.tl_connect.dev.modules.academic_result.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicResultDTO {
    private String studyProgram;
    private List<SemesterResultDTO> semesterResults;
}
