package com.tl_connect.dev.modules.academic_result.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.modules.academic_result.dto.AcademicResultDTO;
import com.tl_connect.dev.modules.academic_result.service.AcademicResultService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/marks")
@RequiredArgsConstructor
public class AcademicResultController {
    private final AcademicResultService resultService;
    

    @GetMapping
    public ResponseEntity<?> getMarks(Authentication authentication,
            @RequestParam(name = "ctdt") String studyProgramCode) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        System.out.println(studentId + " " + studyProgramCode);
        AcademicResultDTO academicResult = resultService.getSubjectResult(studentId, studyProgramCode);
        return ResponseHelper.success("Academic result fetched successfully", academicResult);
    }

    @GetMapping("/export")
    public void exportToExcel(Authentication authentication,
            @RequestParam(name = "ctdt") String studyProgramCode,
            HttpServletResponse response) throws IOException {

        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }

        resultService.exportExcel(userInfo.userId(), studyProgramCode, response);
    }
}
