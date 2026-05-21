package com.tl_connect.dev.modules.enroll.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentPeriodDTO {
    private Long semesterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxCredits;
}
