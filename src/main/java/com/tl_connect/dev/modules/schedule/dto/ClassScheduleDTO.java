package com.tl_connect.dev.modules.schedule.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassScheduleDTO {
    @NotNull(message = "Day of week is required")
    private Integer dayOfWeek;
    @NotNull(message = "Start period is required")
    private Integer startPeriod;
    @NotNull(message = "End period is required")
    private Integer endPeriod;
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    @NotNull(message = "Room is required")
    private String room;


    @AssertTrue(message = "End period must be greater than start period")
    @JsonIgnore
    public boolean isValidPeriodRange() {
        if (startPeriod == null || endPeriod == null) return true;
        return endPeriod > startPeriod;
    }

    @AssertTrue(message = "End time must be greater than start time")
    @JsonIgnore
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) return true;
        return endTime.isAfter(startTime);
    }
}
