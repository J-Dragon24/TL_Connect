package com.tl_connect.dev.modules.study_program.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudyProgramSubDTO {
    private Long semesterId;
    private Boolean isRequired;
    @Size(min = 1, message = "Elective group must be at least 1 character")
    private String electiveGroup;
}
