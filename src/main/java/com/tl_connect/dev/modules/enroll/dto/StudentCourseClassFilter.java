package com.tl_connect.dev.modules.enroll.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseClassFilter {
    private Long facultyId;
    private Long semesterId;
    private Long studentId;
}
