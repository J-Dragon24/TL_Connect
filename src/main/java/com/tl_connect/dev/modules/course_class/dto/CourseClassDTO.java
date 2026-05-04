package com.tl_connect.dev.modules.course_class.dto;

import com.tl_connect.dev.modules.semester.dto.SemesterDTO;

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
public class CourseClassDTO {
    private Long id;
    private String lecturerCode;
    private String lecturerName;
    private String subjectCode;
    private String subjectName;
    private SemesterDTO semester;
    private String classCode;
    private String className;
    private Integer capacity;
    private Integer enrolledCount;
    private Boolean isActive;
}
