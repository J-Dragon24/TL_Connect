package com.tl_connect.dev.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "zalopay")
@Data
public class ZaloPayConfig {
    private int appId;
    private String key1;
    private String key2;
    private String endpointCreate;
    private String endpointGetStatus;
    private String endpointPartialRefund;
    private String endpointQueryRefund;
    private String redirectUrl;
    private String callbackUrl;
}
