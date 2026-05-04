package com.tl_connect.dev.modules.enroll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseClassRequest {
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    @NotNull(message = "Semester ID is required")
    private Long semesterId;
}
