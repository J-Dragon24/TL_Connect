package com.tl_connect.dev.shared.datastructure.intervaltree;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ScheduleInterval {
    private Long classScheduleId;
    private Long courseClassId;
    private String classCode;
    private int dayOfWeek;
    private int startPeriod;
    private int endPeriod;
}
