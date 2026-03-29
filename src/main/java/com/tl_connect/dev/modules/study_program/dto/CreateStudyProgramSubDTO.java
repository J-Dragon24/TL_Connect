package com.tl_connect.dev.modules.study_program.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreateStudyProgramSubDTO {
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    @NotNull(message = "Semester ID is required")
    private Long semesterId;
    @NotNull(message = "Elective group is required")
    private String electiveGroup;
    @NotNull(message = "Is required is required")
    private Boolean isRequired;
}
