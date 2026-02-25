package com.tl_connect.dev.core.common.types;

import java.util.List;

import lombok.Builder;

@Builder
public record JwtPayload(Long userId, List<String> roles, String sign, Long iat, Long exp) {
}