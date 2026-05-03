package com.tl_connect.dev.modules.exam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.exam.ExamService;
import com.tl_connect.dev.modules.exam.dto.ExamScheduleDTO;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    @GetMapping
    public ResponseEntity<?> getExamSchedule(Authentication authentication, @RequestParam(name = "HocKy", required = true) String semesterCode) {
        if(authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)){
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();

        ExamScheduleDTO examScheduleDTO = examService.getExamSchedule(studentId, semesterCode);

        return ResponseHelper.success("Get exam schedule successfully!", examScheduleDTO);
    }
}
