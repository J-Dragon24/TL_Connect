package com.tl_connect.dev.modules.attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.attendance.dto.ClassAttendanceSummaryResponse;
import com.tl_connect.dev.modules.attendance.service.interfaces.AttendanceSessionService;
import com.tl_connect.dev.modules.attendance.service.interfaces.AttendanceStatisticsService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/attendance")
@RequiredArgsConstructor
public class AttendanceAdminController {
    private final AttendanceSessionService attendanceSessionService;
    private final AttendanceStatisticsService attendanceStatisticsService;

    @GetMapping("/session/{classId}")
    public ResponseEntity<?> openSession(Authentication authentication, @PathVariable Long classId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        String token = attendanceSessionService.openSession(classId);
        return ResponseHelper.success("Session opened successfully", token);
    }

    @GetMapping("/statistics/{classId}")
    public ResponseEntity<?> getSession(Authentication authentication, @PathVariable Long classId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        ClassAttendanceSummaryResponse summary = attendanceStatisticsService.getClassSummary(classId);
        return ResponseHelper.success("Get statistics successfully", summary);
    }
}
