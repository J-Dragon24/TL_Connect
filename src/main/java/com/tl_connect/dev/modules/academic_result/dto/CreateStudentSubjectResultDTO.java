package com.tl_connect.dev.modules.academic_result.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateStudentSubjectResultDTO {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @NotNull(message = "Credits is required")
    private Integer credits;

    @NotNull(message = "Attendance score is required")
    private BigDecimal attendanceScore;

    @NotNull(message = "Midterm score is required")
    private BigDecimal midtermScore;

    @NotNull(message = "Final score is required")
    private BigDecimal finalScore;

    @NotNull(message = "Score 10 is required")
    private BigDecimal score10;

    @NotNull(message = "Score 4 is required")
    private BigDecimal score4;

    @NotNull(message = "Letter grade is required")
    private String letterGrade;

    @NotNull(message = "Is pass is required")
    private Boolean isPass;
}
