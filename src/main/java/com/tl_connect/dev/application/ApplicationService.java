package com.tl_connect.dev.application;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final S3Client s3Client;

    @Value("${b2.bucket_name}")
    private String bucketName;

    public String upload(MultipartFile file, String requestType, String content) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String pathToFile = "./" + fileName;
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(pathToFile)
            .contentType(file.getContentType())
            .build();
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return pathToFile;
    }
}
