package com.tl_connect.dev.core.common.ultility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

import com.tl_connect.dev.core.common.types.UserInfo;
import com.tl_connect.dev.core.common.exception.UnauthorizeException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
            throw new UnauthorizeException("Invalid token: " + e.getMessage());
        }
    }

    public UserInfo extractUserInfo(String accessToken) {
        Jwt jwt = verify(accessToken);

        return UserInfo.builder()
            .oid(jwt.getClaimAsString("oid"))
            .email(jwt.getClaimAsString("preferred_username"))
            .name(jwt.getClaimAsString("name"))
            .avatar(fetchAvatar(accessToken))
            .build();
}

    private String fetchAvatar(String accessToken) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(
            "https://graph.microsoft.com/v1.0/me/photo/$value",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            byte[].class
        );
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(response.getBody());
    } catch (Exception e) {
        return null;
    }
}
}
