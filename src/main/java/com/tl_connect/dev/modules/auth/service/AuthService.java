package com.tl_connect.dev.modules.auth.service;

import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.auth.AuthUserRepository;
import com.tl_connect.dev.modules.auth.projection.JwtUserInfoView;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthUserRepository authUserRepository;
    private JwtDecoder jwtDecoder;
    private final String issuer = "https://login.microsoftonline.com/${TENANT_ID}/v2.0";

    public Jwt microsoftTokenVerify(String token) {
        jwtDecoder = JwtDecoders.fromIssuerLocation(issuer);
        return jwtDecoder.decode(token);
    }

    public JwtUserInfoView getUserInfo(String microsoftId) {
        return authUserRepository.findStudentByMicrosoftId(microsoftId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
