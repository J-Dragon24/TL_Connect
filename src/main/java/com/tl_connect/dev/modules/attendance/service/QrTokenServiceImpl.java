package com.tl_connect.dev.modules.attendance.service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crypto.HMACUtil;
import com.tl_connect.dev.modules.attendance.service.interfaces.QrTokenService;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.ErrorException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QrTokenServiceImpl implements QrTokenService {

    @Value("${attendance.secret}")
    private String SECRET;

    @Value("${attendance.expiration}")
    private long EXPIRATION_MINUTES;

    @Override
    public String generate(Long classId, String sessionId) {
        long now = System.currentTimeMillis();
        long expiration = now + EXPIRATION_MINUTES * 60 * 1000;

        String payload = "classId=" + classId + "&sessionId=" + sessionId + "&exp=" + expiration;

        String signature = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, SECRET, payload);

        return payload + "." + signature;
    }

    @Override
    public Map<String, String> verify(String token) {
        String[] parts = token.split("\\.");

        if (parts.length != 2) {
            throw new ErrorException(ResponseStatus.TOKEN_EXPIRED_OR_INVALID, "Invalid token");
        }

        String payload = parts[0];
        String signature = parts[1];

        String expectedSignature = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, SECRET, payload);

        if (!expectedSignature.equals(signature)) {
            throw new ErrorException(ResponseStatus.TOKEN_EXPIRED_OR_INVALID, "Invalid token");
        }

        Map<String, String> map = Arrays.stream(payload.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(
                        a -> a[0],
                        a -> a[1]));

        long expiration = Long.parseLong(map.get("exp"));

        if (expiration < System.currentTimeMillis()) {
            throw new ErrorException(ResponseStatus.TOKEN_EXPIRED_OR_INVALID, "Token expired");
        }

        return map;
    }
}
