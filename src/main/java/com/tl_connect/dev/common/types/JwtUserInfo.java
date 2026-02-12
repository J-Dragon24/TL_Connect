package com.tl_connect.dev.common.types;

import lombok.Builder;

@Builder
public record JwtUserInfo(Long userId, String role) {
}
