package com.tl_connect.dev.modules.application;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.tl_connect.dev.modules.application.dto.ApplicationSubmitDTO;
import com.tl_connect.dev.modules.application.dto.ApplicationTypeDTO;
import java.util.List;

import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.UnauthorizeException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.ultility.BackBlazeProvider;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final BackBlazeProvider fileHelper;

    @GetMapping("/types")
    public ResponseEntity<?> getAllApplicationType() {
        List<ApplicationTypeDTO> applicationTypes = applicationService.getAllApplicationType();
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

}
