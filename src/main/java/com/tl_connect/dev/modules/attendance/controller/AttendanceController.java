package com.tl_connect.dev.modules.attendance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.attendance.dto.AttendanceRequest;
import com.tl_connect.dev.modules.attendance.service.interfaces.AttendanceService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/checkin")
    public ResponseEntity<?> checkin(Authentication authentication, @RequestBody AttendanceRequest request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        attendanceService.checkIn(studentId, request);
        return ResponseHelper.success("Checked in successfully", null);
    }
}
