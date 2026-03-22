package com.tl_connect.dev.modules.student.dto;

import com.tl_connect.dev.core.common.enums.TrainingType;

import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateStudentAcademicDTO {
    @Size(min = 1, message = "Student class code cannot be blank")
    private String studentClassCode;

    @Size(min = 1, message = "Major code cannot be blank")
    private String majorCode;

    @Size(min = 1, message = "Training type cannot be blank")
    private TrainingType trainingType;

    @Size(min = 1, message = "Start year cannot be blank")
    private Integer startYear;

    @Size(min = 1, message = "End year cannot be blank")
    private Integer endYear;

    @Size(min = 1, message = "Cohort cannot be blank")
    private String cohort;

    private String position;
}
