package com.tl_connect.dev.core.common.ultility;

import java.io.Console;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class BackBlazeProvider extends FileHelper {
     private final S3Client s3Client;

    @Value("${b2.bucket_name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        System.out.println("Uploading file: " + file.getOriginalFilename());
        try{
            String key ="uploads/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key;
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}
