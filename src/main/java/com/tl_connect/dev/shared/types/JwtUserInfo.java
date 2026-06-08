package com.tl_connect.dev.shared.types;

import java.security.Principal;
import java.util.List;

import lombok.Builder;

@Builder
public record JwtUserInfo(Long userId, Long oauthUserId, List<String> roles) implements Principal{
    @Override
    public String getName() {
        return userId.toString();
    }
}
