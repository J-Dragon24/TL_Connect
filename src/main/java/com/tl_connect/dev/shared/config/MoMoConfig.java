package com.tl_connect.dev.shared.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "momo")
@Data
public class MoMoConfig {
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String publicKey;
    private String endpoint;
    private String create;
    private String refund;
    private String redirectUrl;
    private String ipnUrl;


    public static String hmacSHA256(String secretKey, String data) {

    try {

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKeySpec =
                new SecretKeySpec(
                        secretKey.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                );

        mac.init(secretKeySpec);

        byte[] hash =
                mac.doFinal(
                        data.getBytes(StandardCharsets.UTF_8)
                );

        StringBuilder hexString =
                new StringBuilder();

        for (byte b : hash) {

            String hex =
                    Integer.toHexString(
                            0xff & b
                    );

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
