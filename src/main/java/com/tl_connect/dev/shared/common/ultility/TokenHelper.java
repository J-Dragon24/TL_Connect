package com.tl_connect.dev.shared.common.ultility;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenHelper {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRefreshToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
