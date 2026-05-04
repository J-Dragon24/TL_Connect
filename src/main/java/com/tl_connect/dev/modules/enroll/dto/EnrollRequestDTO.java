package com.tl_connect.dev.modules.enroll.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollRequestDTO {
    @NotBlank(message = "Study program code is required")
    private String studyProgramCode;

    @NotNull(message = "Course class ID is required")
    private Long courseClassId;
}
