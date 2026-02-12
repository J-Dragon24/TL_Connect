package com.tl_connect.dev.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import com.tl_connect.dev.common.exception.InvalidInputException;
import com.tl_connect.dev.ultility.ResponseHelper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("requestType") String requestType,
            @RequestParam("content") String content
  ) throws IOException {
      if(file.isEmpty()){
          throw new InvalidInputException("File is empty");
      }
      if(requestType.isEmpty()){
          throw new InvalidInputException("Request type is empty");
      }
      if(!file.getOriginalFilename().endsWith(".pdf")){
          throw new InvalidInputException("File is not a PDF");
      }

      String path = applicationService.upload(file, requestType, content);

      return ResponseHelper.success("Application created successfully", path);
    }

}
