package com.tl_connect.dev.modules.oauth.service.interfaces;

import com.tl_connect.dev.modules.oauth.dto.LoginRequestDTO;
import com.tl_connect.dev.modules.oauth.dto.OAuthUserInfoDTO;
import com.tl_connect.dev.modules.oauth.entity.OAuthUser;

public interface OAuthService {

    OAuthUserInfoDTO loginWithMicrosoft(LoginRequestDTO request);

    OAuthUser findById(Long oauthUserId);
}
