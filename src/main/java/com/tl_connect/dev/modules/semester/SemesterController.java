package com.tl_connect.dev.modules.semester;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.exception.UnauthorizeException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.semester.dto.StudentSemesterDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/semester")
@RequiredArgsConstructor
public class SemesterController {
    
    private final SemesterService semesterService;

    @GetMapping("/student")
    public ResponseEntity<?> getAllStudentSemesters(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        List<StudentSemesterDTO> semester = semesterService.getAllStudentSemesters(studentId);
        return ResponseHelper.success("Student semesters retrieved successfully", semester);
    }
}
