package com.tl_connect.dev.core.common.types;

import java.util.List;

import lombok.Builder;

@Builder
public record JwtUserInfo(Long userId, List<String> roles) {
}
