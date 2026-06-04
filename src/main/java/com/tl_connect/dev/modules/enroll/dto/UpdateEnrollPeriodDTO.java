package com.tl_connect.dev.modules.enroll.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEnrollPeriodDTO {
    private Long semesterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int maxCredits;
}
