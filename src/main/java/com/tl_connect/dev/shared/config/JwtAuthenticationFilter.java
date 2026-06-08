package com.tl_connect.dev.shared.config;

import java.io.IOException;
import java.util.List;

import com.tl_connect.dev.modules.oauth.service.interfaces.JWTService;
import com.tl_connect.dev.shared.types.JwtPayload;
import com.tl_connect.dev.shared.types.JwtUserInfo;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.startsWith("/api/v1/oauth2/login") || path.startsWith("/api/v1/payments/callback") || path.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }


        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                JwtUserInfo userInfo = JwtUserInfo.builder()
                        .userId(1L)
                        .oauthUserId(1L)
                        .roles(new ArrayList<>(List.of("ADMIN")))
                        .build();

                List<GrantedAuthority> authorities = userInfo.roles() == null 
                    ? List.of()
                    : userInfo.roles().stream()
                        .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userInfo, null,
                        authorities);

                // JwtPayload payload = jwtService.verifyToken(token);

                // JwtUserInfo userInfo = JwtUserInfo.builder()
                //         .userId(payload.getUserId())
                //         .oauthUserId(payload.getOauthUserId())
                //         .roles(payload.getRoles())
                //         .build();

                // List<GrantedAuthority> authorities = payload.getRoles() == null 
                //     ? List.of()
                //     : payload.getRoles().stream()
                //         .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
                //         .toList();

                // UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userInfo, null,
                //         authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }
}