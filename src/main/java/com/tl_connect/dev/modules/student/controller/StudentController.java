package com.tl_connect.dev.modules.student.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tl_connect.dev.modules.student.dto.SelfUpdateRequestDTO;
import com.tl_connect.dev.modules.student.dto.StudentInfoDTO;
import com.tl_connect.dev.modules.student.service.interfaces.StudentService;
import com.tl_connect.dev.modules.student.service.interfaces.StudentUpdateService;
import com.tl_connect.dev.modules.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.ultility.ResponseHelper;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final StudentUpdateService studentUpdateService;

    @GetMapping("/me")
    public ResponseEntity<?> getStudentInfo(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        StudentInfoDTO studentInfo = studentService.getStudentInfo(studentId);
        return ResponseHelper.success("Student info retrieved successfully", studentInfo);
    }

    @GetMapping("/me/class")
    public ResponseEntity<?> getStudentClassInfo(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        StudentClassInfoDTO studentClassInfo = studentService.getStudentClassInfo(studentId);
        return ResponseHelper.success("Student class info retrieved successfully", studentClassInfo);
    }

    @PostMapping("/me/update")
    public ResponseEntity<?> updateBasicInfo(Authentication authentication, @RequestBody @Valid SelfUpdateRequestDTO dto) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        studentUpdateService.updateSelfInfo(studentId, dto);
        return ResponseHelper.success("Basic info updated successfully", null);
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<?> updateAvatar(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        studentUpdateService.updateAvatar(studentId, file);
        return ResponseHelper.success("Basic info updated successfully", null);
    }
}
