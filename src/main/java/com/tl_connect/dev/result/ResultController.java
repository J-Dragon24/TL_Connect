package com.tl_connect.dev.result;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.common.exception.UnauthorizeException;
import com.tl_connect.dev.common.types.JwtUserInfo;
import com.tl_connect.dev.result.dto.AcademicResultDTO;
import com.tl_connect.dev.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/academic-results")
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;

    @GetMapping("/")
    public ResponseEntity<?> getAcademicResult(Authentication authentication, @RequestParam String trainingProgram) {
        if(authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)){
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        AcademicResultDTO academicResult = resultService.getSubjectResult(studentId, trainingProgram);
        return ResponseHelper.success("Academic result fetched successfully", academicResult);
    }
}
