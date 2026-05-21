package com.tl_connect.dev.modules.oauth.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.oauth.dto.RefreshResponseDTO;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.TokenHelper;
import com.tl_connect.dev.modules.oauth.service.interfaces.JWTService;
import com.tl_connect.dev.modules.oauth.service.interfaces.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final JWTService jwtService;

    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60;

    public RefreshResponseDTO refresh (String refreshToken) {
        Map<String, Object> data = getData(refreshToken);

        if (data == null) {
            throw new InvalidInputException("Invalid refresh token");
        }

        delete(refreshToken);

        String newRefreshToken = TokenHelper.generateRefreshToken();
        save(newRefreshToken, data);

        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
                .userId((Long) data.get("userId"))
                .roles((List<String>) data.get("roles"))
                .build();

        String newAccessToken = jwtService.generateToken(jwtUserInfo);

        return RefreshResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void save(String refreshToken, Map<String, Object> data) {
        String key = "refresh_token:" + refreshToken;
        redisTemplate.opsForValue().set(key, data, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
    }

    public Map<String, Object> getData(String refreshToken) {
        String key = "refresh_token:" + refreshToken;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? (Map<String, Object>) value : null;
    }

    public void delete(String refreshToken) {
        String key = "refresh_token:" + refreshToken;
        redisTemplate.delete(key);
    }
}
