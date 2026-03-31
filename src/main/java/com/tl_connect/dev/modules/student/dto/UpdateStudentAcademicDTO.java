package com.tl_connect.dev.modules.student.dto;

import com.tl_connect.dev.core.common.enums.TrainingType;

import jakarta.validation.constraints.AssertTrue;
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

    private TrainingType trainingType;

    private Integer startYear;

    private Integer endYear;

    @Size(min = 1, message = "Cohort cannot be blank")
    private String cohort;

    private String position;

    @AssertTrue(message = "End year must be greater than start year")
    private boolean isEndYearValid() {
        if (startYear == null || endYear == null) return true;
        return endYear > startYear;
    }
}
