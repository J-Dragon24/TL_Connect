package com.tl_connect.dev.modules.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUserInfoDTO {
    private String microsoftId;
    private String email;
    private String name;
    private String token;
    private String avatar;
}
