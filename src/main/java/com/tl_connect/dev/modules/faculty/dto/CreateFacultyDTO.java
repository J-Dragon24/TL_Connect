package com.tl_connect.dev.modules.faculty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFacultyDTO {
    @NotBlank(message = "Faculty name is required")
    private String facultyName;
    @NotBlank(message = "Faculty code is required")
    private String facultyCode;
}
