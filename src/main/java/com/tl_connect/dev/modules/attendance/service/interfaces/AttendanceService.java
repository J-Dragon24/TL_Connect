package com.tl_connect.dev.modules.attendance.service.interfaces;

import com.tl_connect.dev.modules.attendance.entity.Attendance;

public interface AttendanceService {
    boolean existsBySessionIdAndStudentId(String sessionId, Long studentId);

    void save(Attendance attendance);
}
