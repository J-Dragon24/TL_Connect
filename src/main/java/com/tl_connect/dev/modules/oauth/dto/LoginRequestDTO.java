package com.tl_connect.dev.modules.oauth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotNull(message = "Access token is required")
    private String accessToken;
    private String deviceId;
    private String platform;
    private String fcmToken;
}
