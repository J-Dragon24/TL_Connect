package com.tl_connect.dev.modules.schedule.dto;

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
public class DayOfWeekScheduleDTO {
    int dayOfWeek;
    List<ScheduleCourseClassDTO> courseClasses;
}
