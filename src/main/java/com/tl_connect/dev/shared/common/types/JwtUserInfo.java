package com.tl_connect.dev.shared.common.types;

import java.util.List;

import lombok.Builder;

@Builder
public record JwtUserInfo(Long userId, Long oauthUserId, List<String> roles) {
}
