package com.tl_connect.dev.modules.enroll.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollPeriodDTO {
    @NotNull(message = "Semester ID is required")
    private Long semesterId;
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    @NotNull(message = "Max credits is required")
    @Min(value = 0, message = "Max credits must be greater than or equal to 0")
    private int maxCredits;
}
