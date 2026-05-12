package com.tl_connect.dev.shared.common.ultility;

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

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.UserInfo;

import jakarta.annotation.PostConstruct;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthHelper {

    @Value("${microsoft.tenant-id}")
    private String tenantId;

    @Value("${microsoft.client-id}")
    private String clientId;

    @Value("${microsoft.client-secret}")
    private String clientSecret;

    private JwtDecoder jwtDecoder;
    private final OkHttpClient httpClient;
    private static final String GRAPH_PHOTO_URL = "https://graph.microsoft.com/v1.0/me/photo/$value";

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
        List<String> roles = Optional.ofNullable(jwt.getClaimAsStringList("roles"))
            .orElse(List.of())
            .stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());

        return UserInfo.builder()
            .oid(jwt.getClaimAsString("oid"))
            .email(jwt.getClaimAsString("preferred_username"))
            .name(jwt.getClaimAsString("name"))
            .roles(roles)
            .avatar(fetchAvatar(accessToken))
            .build();
    }

    private String getGraphToken(String accessToken) throws IOException {
        RequestBody body = new FormBody.Builder()
            .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("assertion", accessToken)
            .add("scope", "https://graph.microsoft.com/.default")
            .add("requested_token_use", "on_behalf_of")
            .build();

        Request request = new Request.Builder()
            .url("https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token")
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String json = response.body() != null
                ? response.body().string()
                : "";
            if (!response.isSuccessful()) {
                log.error("Graph token failed: HTTP {} - {}", response.code(), json);
                return "";
            }
            return new ObjectMapper().readTree(json).get("access_token").asText();
        }
        catch (Exception e) {
            log.error("Error fetching graph token", e);
            return "";
        }
    }

    private String fetchAvatar(String accessToken) {
        try {

            String graphToken = getGraphToken(accessToken);

            Request request = new Request.Builder()
            .url(GRAPH_PHOTO_URL)
            .header("Authorization", "Bearer " + graphToken)
            .build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("Fetch avatar failed: HTTP {}", response.code());
                    return "";
                }

                ResponseBody body = response.body();
                if (body == null) {
                    return "";
                }

                String mimeType = body.contentType() != null
                    ? body.contentType().toString()
                    : "image/jpeg";
                String base64 = Base64.getEncoder().encodeToString(body.bytes());
                return "data:" + mimeType + ";base64," + base64;
            }
        } catch (IOException e) {
            log.error("Error fetching avatar", e);
            return "";
        }
    }
}
