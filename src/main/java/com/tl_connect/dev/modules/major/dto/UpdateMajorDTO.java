package com.tl_connect.dev.modules.major.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMajorDTO {
    @Size(min = 1, max = 100, message = "Major name must be between 1 and 100 characters")
    private String majorName;
    @Size(min = 1, max = 10, message = "Major code must be between 1 and 10 characters")
    private String majorCode;
    private Long facultyId;
}
