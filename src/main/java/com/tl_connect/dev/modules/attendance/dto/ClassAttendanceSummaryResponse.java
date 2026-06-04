package com.tl_connect.dev.modules.attendance.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassAttendanceSummaryResponse {
    private String classCode;
    private String className;
    private long totalSessions;
    private List<StudentAttendanceSummary> students;
}
