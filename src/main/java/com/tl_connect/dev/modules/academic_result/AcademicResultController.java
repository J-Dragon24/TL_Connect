package com.tl_connect.dev.modules.academic_result;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.exception.UnauthorizeException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/academic-results")
@RequiredArgsConstructor
public class AcademicResultController {
    private final AcademicResultService resultService;

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
