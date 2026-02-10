package com.tl_connect.dev.exam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.common.dto.ResponseHelper;
import com.tl_connect.dev.common.exception.InvalidInputException;
import com.tl_connect.dev.common.types.JwtUserInfo;
import com.tl_connect.dev.exam.dto.ExamScheduleDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    @GetMapping("/")
    public ResponseEntity<?> getExamSchedule(JwtUserInfo userInfo, @RequestParam String semesterName) {
        try {
            Long studentId = userInfo.userId();
            if (studentId == null) {
                throw new InvalidInputException("Student id is required");
            }

            ExamScheduleDTO examScheduleDTO = examService.getExamSchedule(studentId, semesterName);

            return ResponseHelper.success(semesterName, examScheduleDTO);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }
}
