package com.tl_connect.dev.modules.semester.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSemesterDTO {
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "Academic years must be in the format YYYY-YYYY")
    private String academicYears;

    private String semesterName;

    private String semesterCode;

    @Min(1) @Max(3)
    private Integer semesterNumber;

    private LocalDate startDate;

    private LocalDate endDate;

    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}
