package com.tl_connect.dev.result;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.common.dto.ResponseHelper;
import com.tl_connect.dev.common.exception.InvalidInputException;
import com.tl_connect.dev.common.types.JwtUserInfo;
import com.tl_connect.dev.result.dto.AcademicResultDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/academic-results")
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;

    @GetMapping("/")
    public ResponseEntity<?> getAcademicResult(JwtUserInfo userInfo, @RequestParam String trainingProgram) {
        try {
            Long studentId = userInfo.userId();
            if(studentId == null) {
                throw new InvalidInputException("Student id is required");
            }
            AcademicResultDTO academicResult = resultService.getSubjectResult(studentId, trainingProgram);
            return ResponseHelper.success("Academic result fetched successfully", academicResult);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }
}
