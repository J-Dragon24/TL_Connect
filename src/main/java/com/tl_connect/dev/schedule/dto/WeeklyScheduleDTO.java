package com.tl_connect.dev.schedule.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyScheduleDTO {
    String semester;
    int week;
    LocalDate startDate;
    LocalDate endDate;
    List<DailyScheduleDTO> dailySchedules;
}
