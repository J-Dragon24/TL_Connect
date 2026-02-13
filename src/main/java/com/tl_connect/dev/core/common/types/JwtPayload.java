package com.tl_connect.dev.core.common.types;

import lombok.Builder;

@Builder
public record JwtPayload(Long userId, String role, String sign, Long iat, Long exp) {
}