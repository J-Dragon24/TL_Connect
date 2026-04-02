package com.tl_connect.dev.modules.oauth;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.oauth.dto.LoginRequestDTO;
import com.tl_connect.dev.modules.oauth.dto.OAuthUserInfoDTO;
import com.tl_connect.dev.modules.oauth.service.OAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oauthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        if(request.getAccessToken() == null || request.getAccessToken().isEmpty()){
            throw new InvalidInputException("token is required and must be non-empty string");
        }

        OAuthUserInfoDTO userInfo = oauthService.loginWithMicrosoft(request);

        return ResponseHelper.success("Login successful", userInfo);    
    }
}
