package com.tl_connect.dev.modules.subject.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubjectDTO implements StudyDTOInterface {
    @NotNull(message = "Faculty ID is required")
    private Long facultyId;
    private Long departmentId;
    @NotBlank(message = "Subject code is required")
    private String subjectCode;
    @NotBlank(message = "Subject name is required")
    private String subjectName;
    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be >= 1")
    private Integer credits;
    @NotNull(message = "Coefficient is required")
    @Min(value = 1, message = "Coefficient must be >= 1")
    private BigDecimal coefficient;
    @NotNull(message = "Lecture hours is required")
    @Min(value = 1, message = "Lecture hours must be >= 1")
    private Integer lectureHours;
    @NotNull(message = "Practice hours is required")
    @Min(value = 1, message = "Practice hours must be >= 1")
    private Integer practiceHours;
}
