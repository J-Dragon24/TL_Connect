package com.tl_connect.dev.modules.attendance.service.interfaces;

import com.tl_connect.dev.modules.attendance.dto.AttendanceRequest;

public interface AttendanceService {
    void checkIn(Long studentId, AttendanceRequest request);
}
