package com.tl_connect.dev.modules.attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.attendance.dto.ClassAttendanceSummaryResponse;
import com.tl_connect.dev.modules.attendance.service.interfaces.AttendanceSessionService;
import com.tl_connect.dev.modules.attendance.service.interfaces.AttendanceStatisticsService;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/attendance")
@RequiredArgsConstructor
public class AttendanceAdminController {
    private final AttendanceSessionService attendanceSessionService;
    private final AttendanceStatisticsService attendanceStatisticsService;

    @GetMapping("/session/{classId}")
    public ResponseEntity<?> openSession(@PathVariable Long classId) {
        String token = attendanceSessionService.openSession(classId);
        return ResponseHelper.success("Session opened successfully", token);
    }

    @GetMapping("/statistics/{classId}")
    public ResponseEntity<?> getSession(@PathVariable Long classId) {
        ClassAttendanceSummaryResponse summary = attendanceStatisticsService.getClassSummary(classId);
        return ResponseHelper.success("Get statistics successfully", summary);
    }
}
