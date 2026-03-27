package com.tl_connect.dev.modules.schedule.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateScheduleDTO {
    @Min(1)
    @Max(7)
    private Integer dayOfWeek;
    @Min(1)
    private Integer startPeriod;
    @Min(1)
    private Integer endPeriod;
    private LocalTime startTime;
    private LocalTime endTime;
    @Size(min = 1, max = 10)
    private String room;


    @AssertTrue(message = "End period must be greater than start period")
    public boolean isValidPeriodRange() {
        if (startPeriod == null || endPeriod == null) return true;
        return endPeriod > startPeriod;
    }

    @AssertTrue(message = "End time must be greater than start time")
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) return true;
        return endTime.isAfter(startTime);
    }
}
