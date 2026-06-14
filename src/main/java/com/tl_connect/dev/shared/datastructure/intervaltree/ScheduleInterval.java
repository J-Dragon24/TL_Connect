package com.tl_connect.dev.shared.datastructure.intervaltree;

import com.tl_connect.dev.modules.enroll.projection.ScheduleIntervalRow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleInterval {
    private Long classScheduleId;
    private Long courseClassId;
    private String classCode;
    private int dayOfWeek;
    private int startPeriod;
    private int endPeriod;

    public static ScheduleInterval from(ScheduleIntervalRow row) {
        return ScheduleInterval.builder()
                .classScheduleId(row.getClassScheduleId())
                .courseClassId(row.getCourseClassId())
                .classCode(row.getClassCode())
                .dayOfWeek(row.getDayOfWeek())
                .startPeriod(row.getStartPeriod())
                .endPeriod(row.getEndPeriod())
                .build();
    }
}
