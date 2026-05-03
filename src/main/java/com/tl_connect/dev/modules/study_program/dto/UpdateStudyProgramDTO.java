package com.tl_connect.dev.modules.study_program.dto;

import com.tl_connect.dev.shared.common.enums.TrainingType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudyProgramDTO {
    @Size(min = 1, max = 20, message = "Study program code must be between 1 and 20 characters")
    private String studyProgramCode;
    @Size(min = 1, max = 255, message = "Study program name must be between 1 and 255 characters")
    private String studyProgramName;
    private Long majorId;
    private Integer startYear;
    @Min(value = 1, message = "Total credits must be greater than or equal to 1")
    private Integer totalCredits;
    private TrainingType trainingType;
}
