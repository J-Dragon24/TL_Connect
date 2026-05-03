package com.tl_connect.dev.modules.course_class.dto;

import lombok.Data;

@Data
public class CourseClassForEnrollDTO {
    private Long id;
    private String classCode;
    private Long subjectId;
    private Long semesterId;
    private Long classScheduleId;
    private Integer dayOfWeek;
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer credits;
    private Integer capacity;
}
