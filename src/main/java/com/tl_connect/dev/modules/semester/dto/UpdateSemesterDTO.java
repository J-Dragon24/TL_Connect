package com.tl_connect.dev.modules.semester.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSemesterDTO {
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic years must be in the format YYYY-YYYY")
    private String academicYears;

    @Size(min = 1, max = 10, message = "Semester name must be between 1 and 10 characters")
    private String semesterName;

    @Size(min = 1, max = 10, message = "Semester code must be between 1 and 10 characters")
    private String semesterCode;

    @Min(1) @Max(3)
    private Integer semesterNumber;

    private LocalDate startDate;

    private LocalDate endDate;
}
