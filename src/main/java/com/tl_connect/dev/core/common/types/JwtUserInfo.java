package com.tl_connect.dev.core.common.types;

import lombok.Builder;

@Builder
public record JwtUserInfo(Long userId, String role) {
}
