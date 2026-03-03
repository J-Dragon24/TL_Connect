package com.tl_connect.dev.modules.auth;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.auth.dto.OAuthUserInfoDTO;
import com.tl_connect.dev.modules.auth.service.OAuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class AuthController {

    private final OAuthService oauthService;

    public record LoginRequest(String accessToken) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if(request.accessToken() == null || request.accessToken().isEmpty()){
            throw new InvalidInputException("token is required and must be non-empty string");
        }

        OAuthUserInfoDTO userInfo = oauthService.loginWithMicrosoft(request.accessToken());

        return ResponseHelper.success("Login successful", userInfo);    
    }
}
