package com.tl_connect.dev.modules.enroll.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollRequestDTO {
    @NotNull(message = "Study program code is required")
    private Long studyProgramId;

    @NotNull(message = "Course class ID is required")
    private Long courseClassId;
}
