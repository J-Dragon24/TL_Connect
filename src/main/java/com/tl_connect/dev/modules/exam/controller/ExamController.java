package com.tl_connect.dev.modules.exam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.exception.UnauthorizeException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.exam.ExamService;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<?> getExamSchedule(Authentication authentication, @RequestParam(name = "HocKy", required = true) String semesterName) {
        if(authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)){
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();

        ExamScheduleDTO examScheduleDTO = examService.getExamSchedule(studentId, semesterName);

        return ResponseHelper.success(semesterName, examScheduleDTO);
    }
}
