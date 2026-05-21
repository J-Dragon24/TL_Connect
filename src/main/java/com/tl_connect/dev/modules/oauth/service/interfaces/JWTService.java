package com.tl_connect.dev.modules.oauth.service.interfaces;

import com.tl_connect.dev.shared.common.types.JwtPayload;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;

public interface JWTService {

    String generateToken(JwtUserInfo userInfo);

    JwtPayload verifyToken(String token);
}
