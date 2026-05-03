package com.tl_connect.dev.modules.study_program.dto;

import com.tl_connect.dev.shared.common.enums.TrainingType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStudyProgramDTO {
    @NotNull(message = "Study program code is required")
    private String studyProgramCode;
    @NotNull(message = "Study program name is required")
    private String studyProgramName;
    @NotNull(message = "Major id is required")
    private Long majorId;
    @NotNull(message = "Start year is required")
    private Integer startYear;
    @NotNull(message = "Total credits is required")
    private Integer totalCredits;
    @NotNull(message = "Training type is required")
    private TrainingType trainingType;
}
