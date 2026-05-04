package com.tl_connect.dev.modules.schedule.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassScheduleAdminDTO {
    private Long id;
    private Integer dayOfWeek;
    private Integer startPeriod;
    private Integer endPeriod;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;

}
