package com.tl_connect.dev.modules.oauth.service.interfaces;

import com.tl_connect.dev.shared.types.JwtPayload;
import com.tl_connect.dev.shared.types.JwtUserInfo;

public interface JWTService {

    String generateToken(JwtUserInfo userInfo);

    JwtPayload verifyToken(String token);
}
