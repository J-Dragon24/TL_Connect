package com.tl_connect.dev.modules.auth;

import java.io.IOException;
import java.util.List;

import com.tl_connect.dev.core.common.types.JwtPayload;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.modules.auth.service.JWTService;

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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.startsWith("/oauth2/") || path.startsWith("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                JwtPayload payload = jwtService.verifyToken(token);

                JwtUserInfo userInfo = JwtUserInfo.builder()
                        .userId(payload.userId())
                        .roles(payload.roles())
                        .build();

                List<GrantedAuthority> authorities = payload.roles().stream()
                        .map(r -> new SimpleGrantedAuthority("APPROLE_" + r))
                        .toList();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userInfo, null,
                        authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        }
        filterChain.doFilter(request, response);
    }
}
