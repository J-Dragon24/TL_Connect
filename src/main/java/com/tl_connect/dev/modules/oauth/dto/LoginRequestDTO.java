package com.tl_connect.dev.modules.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    private String accessToken;
    private String deviceId;
    private String platform;
}
