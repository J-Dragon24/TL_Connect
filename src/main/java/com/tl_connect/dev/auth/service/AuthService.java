package com.tl_connect.dev.auth.service;

import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.auth.AuthUserRepository;
import com.tl_connect.dev.auth.projection.JwtUserInfoView;

@Service
public class AuthService {
    private final AuthUserRepository authUserRepository;
    private JwtDecoder jwtDecoder;
    private final String issuer = "https://login.microsoftonline.com/${TENANT_ID}/v2.0";

    public Jwt microsoftTokenVerify(String token) {
        jwtDecoder = JwtDecoders.fromIssuerLocation(issuer);
        return jwtDecoder.decode(token);
    }

    public AuthService(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public JwtUserInfoView getUserInfo(String microsoftId) {
        return authUserRepository.findStudentByMicrosoftId(microsoftId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
