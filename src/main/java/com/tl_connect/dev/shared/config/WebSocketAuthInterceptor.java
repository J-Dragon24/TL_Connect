package com.tl_connect.dev.shared.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.tl_connect.dev.modules.oauth.service.interfaces.JWTService;
import com.tl_connect.dev.shared.types.JwtPayload;
import com.tl_connect.dev.shared.types.JwtUserInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor{
    private final JWTService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.out.println(">>> STOMP CONNECT received");
            System.out.println(">>> Headers: " + accessor.toNativeHeaderMap());
            String header= accessor.getFirstNativeHeader("Authorization");

            if(header == null || !header.startsWith("Bearer ")) {
                throw new MessagingException("Unauthorized websocket");
            }

            String token = header.substring(7);

            try{

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

                // JwtPayload jwtPayload = jwtService.verifyToken(token);

                // JwtUserInfo userInfo = JwtUserInfo.builder()
                // .userId(jwtPayload.getUserId())
                // .oauthUserId(jwtPayload.getOauthUserId())
                // .roles(jwtPayload.getRoles())
                // .build();

                // List<GrantedAuthority> authorities = jwtPayload.getRoles() == null
                //     ?List.of()
                //     :jwtPayload.getRoles().stream().map(
                //         r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r)
                //     ).toList();

                // Authentication auth = new UsernamePasswordAuthenticationToken(userInfo, null, authorities);


                accessor.setUser(auth);
                
            } catch (Exception e) {
                throw new MessagingException("Invalid token");
            }
        }

        return message;
    }
}
