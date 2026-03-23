package com.tl_connect.dev.modules.study_program.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MajorDTO {
    @NotBlank(message = "Major name is required")
    private String majorName;
    @NotBlank(message = "Major code is required")
    private String majorCode;
    @NotBlank(message = "Faculty is required")
    private String faculty;
}
