package com.tl_connect.dev.config;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tl_connect.dev.common.exception.InvalidInputException;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class B2Config {

    @Value("${b2.end_point}")
    private String endPointUrl;


    @Value("${b2.aws_access_key_id}")
    private String awsAccessKeyId;

    @Value("${b2.aws_secret_access_key}")
    private String awsSecretAccessKey;

    @Bean
    public S3Client s3Client() {
        Matcher matcher = Pattern.compile("https://s3\\.([a-z0-9-]+)\\.backblazeb2\\.com").matcher(endPointUrl);
        if(!matcher.find()){
            throw new InvalidInputException("Invalid endpoint URL");
        }
        String region = matcher.group(1);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey)))
                .endpointOverride(URI.create(endPointUrl))
                .build();
    }
}

