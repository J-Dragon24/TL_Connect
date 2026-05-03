package com.tl_connect.dev.modules.enroll.dto;

import com.tl_connect.dev.modules.course_class.dto.CourseClassForEnrollDTO;

public record ScheduleForCheckDTO(
    int dayOfWeek,
    int startPeriod,
    int endPeriod
) {
    public static ScheduleForCheckDTO from(CourseClassForEnrollDTO dto) {
        return new ScheduleForCheckDTO(
            dto.getDayOfWeek(),
            dto.getStartPeriod(),
            dto.getEndPeriod()
        );
    }
}
