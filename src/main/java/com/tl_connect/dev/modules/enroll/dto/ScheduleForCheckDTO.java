package com.tl_connect.dev.modules.enroll.dto;

import com.tl_connect.dev.modules.enroll.projection.DetailsForCheckEnrollRow;

public record ScheduleForCheckDTO(
    int dayOfWeek,
    int startPeriod,
    int endPeriod
) {
    public static ScheduleForCheckDTO from(DetailsForCheckEnrollRow dto) {
        return new ScheduleForCheckDTO(
            dto.getDayOfWeek(),
            dto.getStartPeriod(),
            dto.getEndPeriod()
        );
    }
}
