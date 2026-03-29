package com.tl_connect.dev.modules.academic_result.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudentSubjectResultDTO {

    private Long semesterId;

    private BigDecimal attendanceScore;

    private BigDecimal midtermScore;

    @Max(value = 10)
    @Min(value = 0)
    private BigDecimal finalScore;

    @Max(value = 10)
    @Min(value = 0)
    private BigDecimal score10;

    @Max(value = 4)
    @Min(value = 0)
    private BigDecimal score4;

    @Pattern(regexp = "^[A-F]$")
    private String letterGrade;

    private Boolean isPass;
}
