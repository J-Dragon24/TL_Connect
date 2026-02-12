package com.tl_connect.dev.exam;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.common.exception.UnauthorizeException;
import com.tl_connect.dev.common.types.JwtUserInfo;
import com.tl_connect.dev.exam.dto.ExamScheduleDTO;
import com.tl_connect.dev.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    @GetMapping("/")
    public ResponseEntity<?> getExamSchedule(Authentication authentication, @RequestParam String semesterName) {
        if(authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)){
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();

        ExamScheduleDTO examScheduleDTO = examService.getExamSchedule(studentId, semesterName);

        return ResponseHelper.success(semesterName, examScheduleDTO);
    }
}
