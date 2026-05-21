package com.tl_connect.dev.modules.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.tl_connect.dev.modules.application.dto.ApplicationSubmitDTO;
import com.tl_connect.dev.modules.application.dto.ApplicationTypeDTO;
import com.tl_connect.dev.modules.application.dto.HistoryApplicationDTO;
import com.tl_connect.dev.modules.application.dto.HistoryDetailApplication;
import com.tl_connect.dev.modules.application.service.interfaces.ApplicationService;
import com.tl_connect.dev.modules.application.service.interfaces.ApplicationTypeService;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;
import com.tl_connect.dev.shared.common.ultility.provider.BackBlazeProvider;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final ApplicationTypeService applicationTypeService;
    private final BackBlazeProvider fileHelper;

    @GetMapping("/types")
    public ResponseEntity<?> getAllApplicationType() {
        List<ApplicationTypeDTO> applicationTypes = applicationTypeService.getAllApplicationType();
        return ResponseHelper.success("List of applications", applicationTypes);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(
            Authentication authentication,
            @RequestParam("file") List<MultipartFile> files,
            @RequestParam("application-type") Long applicationType,
            @RequestParam(value = "content", required = false) String content) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new InvalidInputException("File is empty");
        }
        if (applicationType == null) {
            throw new InvalidInputException("Application type is empty");
        }

        for (MultipartFile f : files) {
            if (!fileHelper.isPDF(f)) {
                throw new InvalidInputException("Only valid PDF files are allowed");
            }
            if (f.getSize() > 5 * 1024 * 1024) {
                throw new InvalidInputException("File size should be less than 5MB");
            }
        }

        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();

        ApplicationSubmitDTO result = applicationService.submitApplication(files, applicationType, content, studentId);

        if(result.getSuccess()){
            return ResponseHelper.success("Application created successfully", result.getFileNames());
        }else{
            return ResponseHelper.internalError("Application failed to create");
        }
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<?> getDetailApplicationHistory(Authentication authentication, @PathVariable("id") Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        HistoryDetailApplication application = applicationService.getDetailApplicationHistory(id);
        return ResponseHelper.success("Application detail", application);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistoryApplication(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        List<HistoryApplicationDTO> applications = applicationService.getHistoryApplication(studentId);
        return ResponseHelper.success("List of applications", applications);
    }

}
