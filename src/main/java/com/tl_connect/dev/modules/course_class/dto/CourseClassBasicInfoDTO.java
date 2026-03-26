package com.tl_connect.dev.modules.course_class.dto;

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
public class CourseClassBasicInfoDTO {
    private Long id;
    private String classCode;
    private String className;
    private Integer capacity;
    private String lecturerCode;
    private String subjectCode;
    private String semesterCode;
    private Boolean isActive;
}
