package com.tl_connect.dev.modules.faculty.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFacultyDTO {
    @Size(min = 1, max = 100, message = "Faculty name must be between 1 and 100 characters")
    private String facultyName;
    @Size(min = 1, max = 10, message = "Faculty code must be between 1 and 10 characters")
    private String facultyCode;
}
