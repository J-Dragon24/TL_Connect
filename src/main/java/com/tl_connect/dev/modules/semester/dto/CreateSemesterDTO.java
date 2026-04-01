package com.tl_connect.dev.modules.semester.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSemesterDTO {
    @NotBlank(message = "Academic years is required")
    private String academicYears;
    @NotBlank(message = "Semester name is required")
    private String semesterName;
    @NotBlank(message = "Semester code is required")
    private String semesterCode;
    @NotNull(message = "Semester number is required")
    @Min(1) @Max(3)
    private int semesterNumber;
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateValid() {
        return endDate.isAfter(startDate);
    }
}
