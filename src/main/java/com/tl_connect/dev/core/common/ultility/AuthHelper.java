package com.tl_connect.dev.core.common.ultility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.tl_connect.dev.core.common.exception.UnauthorizeException;

import jakarta.annotation.PostConstruct;

@Component
public class AuthHelper {

    @Value("${microsoft.tenant-id}")
    private String tenantId;

    @Value("${microsoft.client-id}")
    private String clientId;

    private JwtDecoder jwtDecoder;

    @PostConstruct
    public void init() {
        String issuer = "https://login.microsoftonline.com/" + tenantId + "/v2.0";
        NimbusJwtDecoder decoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = token ->
            token.getAudience().contains(clientId)
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience", null));

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(issuer),
            audienceValidator
        ));

        this.jwtDecoder = decoder;
    }

    public Jwt verify(String accessToken) {
        try {
            return jwtDecoder.decode(accessToken);
        } catch (JwtException e) {
            e.printStackTrace(); // xem lỗi thật
            throw new UnauthorizeException("Invalid token: " + e.getMessage());
        }
    }
}
