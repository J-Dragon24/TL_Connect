package com.tl_connect.dev.modules.major.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMajorDTO {
    @NotBlank(message = "Major code is required")
    private String majorCode;
    @NotBlank(message = "Major name is required")
    private String majorName;
    @NotNull(message = "Faculty ID is required")
    private Long facultyId;
}
