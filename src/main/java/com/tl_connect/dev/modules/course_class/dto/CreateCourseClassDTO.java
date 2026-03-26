package com.tl_connect.dev.modules.course_class.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseClassDTO {
    @NotBlank(message = "Class code is required")
    private String classCode;

    @NotBlank(message = "Class name is required")
    private String className;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    private Long lecturerId;
}
