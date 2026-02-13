package com.tl_connect.dev.modules.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.ultility.BackBlazeProvider;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final BackBlazeProvider fileHelper;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(
            @RequestParam("file") MultipartFile file,
            @RequestParam("requestType") String requestType,
            @RequestParam("content") String content) throws IOException {
        if (file.isEmpty()) {
            throw new InvalidInputException("File is empty");
        }
        if (requestType == null || requestType.isBlank()) {
            throw new InvalidInputException("Request type is empty");
        }
        if (!fileHelper.isPDF(file)) {
            throw new InvalidInputException("Only valid PDF files are allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidInputException("File size should be less than 5MB");
        }

        String path = applicationService.submitApplication(file, requestType, content, fileHelper);

        return ResponseHelper.success("Application created successfully", path);
    }

}
