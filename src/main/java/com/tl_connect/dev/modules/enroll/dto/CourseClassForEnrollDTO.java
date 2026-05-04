package com.tl_connect.dev.modules.enroll.dto;

import java.time.LocalTime;
import java.util.List;

import com.tl_connect.dev.modules.enroll.projection.CourseClassForEnrollRow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseClassForEnrollDTO {
    private Long id;
    private String lecturerCode;
    private String lecturerName;
    private String classCode;
    private String className;
    private Integer capacity;
    private Integer enrolledCount;
    private List<ScheduleForEnrollDTO> schedules;
}
