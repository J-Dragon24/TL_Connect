package com.tl_connect.dev.modules.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceSummary {
    private String studentCode;

    private String studentName;

    private long presentCount;

    private long absentCount;

    private double attendanceRate;
}
