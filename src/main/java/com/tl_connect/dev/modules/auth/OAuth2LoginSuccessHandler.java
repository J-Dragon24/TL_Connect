package com.tl_connect.dev.modules.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.modules.auth.projection.JwtUserInfoView;
import com.tl_connect.dev.modules.auth.service.AuthService;
import com.tl_connect.dev.modules.auth.service.JWTService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    
    private final JWTService jwtService;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String micrsoftId = oidcUser.getAttributes().get("oid").toString();

        JwtUserInfoView jwtUserInfoView = authService.getUserInfo(micrsoftId);

        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
        .userId(jwtUserInfoView.getStudentId())
        .role(jwtUserInfoView.getRole())
        .build();
        
        String token = jwtService.generateToken(jwtUserInfo);
        
        String redirectUrl = "myapp://login-success?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
