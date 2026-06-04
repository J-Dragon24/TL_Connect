package com.tl_connect.dev.modules.oauth.service.interfaces;

import java.util.Map;

import com.tl_connect.dev.modules.oauth.dto.RefreshResponseDTO;

public interface RefreshTokenService {

    RefreshResponseDTO refresh(String refreshToken);

    void save(String refreshToken, Map<String, Object> data);

    Map<String, Object> getData(String refreshToken);

    void delete(String refreshToken);
}
