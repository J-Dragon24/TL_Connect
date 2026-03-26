package com.tl_connect.dev.modules.department.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDepartmentDTO {
    @Size(min = 1, max = 10, message = "Department code must be between 1 and 10 characters")
    private String departmentCode;
    @Size(min = 1, max = 255, message = "Department name must be between 1 and 255 characters")
    private String departmentName;
    private Long facultyId;
}
