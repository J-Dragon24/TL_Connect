package com.tl_connect.dev.modules.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDepartmentDTO {
    @NotBlank(message = "Department code is required")
    private String departmentCode;
    @NotBlank(message = "Department name is required")
    private String departmentName;
    @NotNull(message = "Faculty ID is required")
    private Long facultyId;
}
