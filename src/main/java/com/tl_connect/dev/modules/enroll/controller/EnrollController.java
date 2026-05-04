package com.tl_connect.dev.modules.enroll.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.enroll.dto.DropRequestDTO;
import com.tl_connect.dev.modules.enroll.dto.EnrollRequestDTO;
import com.tl_connect.dev.modules.enroll.service.EnrollService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/enroll")
@RequiredArgsConstructor
public class EnrollController {
    private final EnrollService enrollService;

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollInCourseClass(Authentication authentication,
            @RequestBody @Valid EnrollRequestDTO request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        enrollService.enroll(studentId, request.getCourseClassId(), request.getStudyProgramCode());
        return ResponseHelper.success("Đăng ký thành công", null);
    }

    @PostMapping("/drop")
    public ResponseEntity<?> dropCourseClass(Authentication authentication,
            @RequestBody @Valid DropRequestDTO request) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        enrollService.drop(studentId, request.getCourseClassId());
        return ResponseHelper.success("Hủy đăng ký thành công", null);
    }
}
