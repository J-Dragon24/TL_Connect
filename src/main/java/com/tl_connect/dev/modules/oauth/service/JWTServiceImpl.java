package com.tl_connect.dev.modules.oauth.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtPayload;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.modules.oauth.service.interfaces.JWTService;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.secret}")
    private String SECRET;

    private final long EXPIRATION = 60 * 60 * 1000L;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ALGORITHM = "HmacSHA256";

    public String generateToken(JwtUserInfo userInfo) {
        long now = System.currentTimeMillis();

        JwtPayload payload = JwtPayload.builder()
                .userId(userInfo.userId())
                .oauthUserId(userInfo.oauthUserId())
                .roles(userInfo.roles())
                .iat(now)
                .exp(now + EXPIRATION)
                .build();

        return signJWT(payload);
    }

    public JwtPayload verifyToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3)
            throw new InvalidInputException("Invalid JWT format");

        JwtPayload payload = verifyJWT(token);
        if (payload.getExp() < System.currentTimeMillis()) {
            throw new UnauthorizeException("Token expired");
        }
        return payload;
    }

    private String signJWT(JwtPayload payload) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payloadJson = objectMapper.writeValueAsString(payload);

            String headerBase64 = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
            String payloadBase64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

            String signatureData = headerBase64 + "." + payloadBase64;
            byte[] signature = hmacSha256(signatureData, SECRET);
            String encodedSignature = base64UrlEncode(signature);

            return signatureData + "." + encodedSignature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT", e);
        }

    }

    private JwtPayload verifyJWT(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new InvalidInputException("Invalid JWT format");
            }

            String headerBase64 = parts[0];
            String payloadBase64 = parts[1];
            String signatureBase64 = parts[2];

            String signatureData = headerBase64 + "." + payloadBase64;
            byte[] expectedSignature = hmacSha256(signatureData, SECRET);
            String expectedEncodedSignature = base64UrlEncode(expectedSignature);

            if (!expectedEncodedSignature.equals(signatureBase64)) {
                throw new UnauthorizeException("Invalid JWT signature");
            }

            return objectMapper.readValue(base64UrlDecode(payloadBase64), JwtPayload.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify JWT", e);
        }
    }

    private byte[] hmacSha256(String data, String key) throws Exception {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(keySpec);

            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC SHA256 hash", e);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String base64UrlDecode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString), StandardCharsets.UTF_8);
    }
}
