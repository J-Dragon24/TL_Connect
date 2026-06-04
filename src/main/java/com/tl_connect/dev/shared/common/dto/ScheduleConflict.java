package com.tl_connect.dev.shared.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleConflict {
    private int dayOfWeek;
    private int startPeriod;
    private int endPeriod;
    private String classOverlapCode;
}
