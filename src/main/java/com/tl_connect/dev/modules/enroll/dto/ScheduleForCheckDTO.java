package com.tl_connect.dev.modules.enroll.dto;

public record ScheduleForCheckDTO(
    int dayOfWeek,
    int startPeriod,
    int endPeriod
) {
    public static ScheduleForCheckDTO from(DetailsForCheckEnrollDTO dto) {
        return new ScheduleForCheckDTO(
            dto.getDayOfWeek(),
            dto.getStartPeriod(),
            dto.getEndPeriod()
        );
    }
}
