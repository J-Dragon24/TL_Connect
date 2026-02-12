package com.tl_connect.dev.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tl_connect.dev.common.types.JwtUserInfo;
import com.tl_connect.dev.common.exception.UnauthorizeException;
import com.tl_connect.dev.student.dto.StudentInfoDTO;
import com.tl_connect.dev.student_class.dto.StudentClassInfoDTO;
import com.tl_connect.dev.ultility.ResponseHelper;

import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentInfoService studentService;

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
}
