package com.tl_connect.dev.auth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.tl_connect.dev.auth.service.JWTService;
import com.tl_connect.dev.common.exception.NotFoundException;
import com.tl_connect.dev.auth.projection.JwtUserInfoView;
import com.tl_connect.dev.common.types.JwtUserInfo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    
    private final JWTService jwtService;
    private final AuthUserRepository authUserRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String micrsoftId = oidcUser.getAttributes().get("oid").toString();

        JwtUserInfoView jwtUserInfoView = authUserRepository.findStudentByMicrosoftId(micrsoftId)
        .orElseThrow(() -> new NotFoundException("User not found"));

        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
        .userId(jwtUserInfoView.getStudentId())
        .role(jwtUserInfoView.getRole())
        .build();
        
        String token = jwtService.generateToken(jwtUserInfo);
        
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }
}
