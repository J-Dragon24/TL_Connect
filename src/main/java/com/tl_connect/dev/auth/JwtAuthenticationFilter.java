package com.tl_connect.dev.auth;

import java.io.IOException;
import java.util.List;

import com.tl_connect.dev.auth.service.JWTService;
import com.tl_connect.dev.common.types.JwtPayload;
import com.tl_connect.dev.common.types.JwtUserInfo;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);
            try{
                JwtPayload payload = jwtService.verifyToken(token);

                JwtUserInfo userInfo = JwtUserInfo.builder()
                .userId(payload.userId())
                .role(payload.role())
                .build();

                UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userInfo, null, List.of(()-> "ROLE_" + payload.role()));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }catch(Exception e){
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }


        }
        filterChain.doFilter(request, response);
    }
}
