package com.tl_connect.dev.modules.attendance.service.interfaces;

import com.tl_connect.dev.modules.attendance.dto.ClassAttendanceSummaryResponse;

public interface AttendanceStatisticsService {
    ClassAttendanceSummaryResponse getClassSummary(Long courseClassId);
}
