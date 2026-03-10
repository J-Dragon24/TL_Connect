package com.tl_connect.dev.core.common.types;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtPayload {
    private Long userId;
    private List<String> roles;
    private String sign;
    private Long iat;
    private Long exp;
}