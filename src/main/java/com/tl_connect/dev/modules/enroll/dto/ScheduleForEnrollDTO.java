package com.tl_connect.dev.modules.enroll.dto;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleForEnrollDTO {
    private Integer dayOfWeek;
    private Integer startPeriod;
    private Integer endPeriod;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
}
