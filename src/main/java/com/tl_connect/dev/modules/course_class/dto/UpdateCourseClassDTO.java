package com.tl_connect.dev.modules.course_class.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCourseClassDTO {
    @Size(min = 1, max = 100, message = "Class code must be between 1 and 100 characters")
    private String classCode;
    @Size(min = 1, max = 255, message = "Class name must be between 1 and 255 characters")
    private String className;
    @Min(value = 1, message = "Capacity must be greater than or equal to 1")
    private Integer capacity;
    
    private Long subjectId;
    private Long semesterId;
    private Long lecturerId;
}
