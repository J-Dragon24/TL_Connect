package com.tl_connect.dev.modules.study_program.dto;

import com.tl_connect.dev.core.common.enums.TrainingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyProgramAdmDTO {
    private Long id;
    private String studyProgramCode;
    private String studyProgramName;
    private String majorCode;
    private Integer startYear;
    private Integer totalCredits;
    private TrainingType trainingType;
}
