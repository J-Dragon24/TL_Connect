package com.tl_connect.dev.modules.attendance.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceResponse {
    private String studentCode;

    private String fullName;

    private LocalDateTime checkInTime;
}
