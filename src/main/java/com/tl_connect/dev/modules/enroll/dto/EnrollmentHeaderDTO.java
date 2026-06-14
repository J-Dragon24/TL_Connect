package com.tl_connect.dev.modules.enroll.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentHeaderDTO {
    private Long semesterId;
    private String semesterName;
    private String semesterCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
