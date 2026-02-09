package com.tl_connect.dev.auth;

import java.security.Key;

import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.auth.projection.JwtUserInfoView;

@Service
public class AuthService {
    private final AuthUserRepository authUserRepository;
    private JwtDecoder jwtDecoder;
    private final String issuer = "https://login.microsoftonline.com/{TENANT_ID}/v2.0";
    private static final String SECRET = "MY_SUPER_SECRET_KEY_123456789_MY_SUPER_SECRET";
    private static final long EXPIRATION = 60 * 60 * 1000;

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
