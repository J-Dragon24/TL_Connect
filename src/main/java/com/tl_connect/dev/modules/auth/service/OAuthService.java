package com.tl_connect.dev.modules.auth.service;

import java.util.List;

import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.ultility.AuthHelper;
import com.tl_connect.dev.modules.auth.AuthUserRepository;
import com.tl_connect.dev.modules.auth.projection.JwtUserInfoView;
import com.tl_connect.dev.modules.auth.dto.OAuthUserInfoDTO;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final AuthUserRepository authUserRepository;
    
    private final AuthHelper authHelper;

    private final JWTService jwtService;

    public OAuthUserInfoDTO loginWithMicrosoft(String accessToken){
        Jwt jwt = authHelper.verify(accessToken);
        String microsoftId = jwt.getClaimAsString("oid");
        String email = jwt.getClaimAsString("preferred_username");
        String name = jwt.getClaimAsString("name");
        List<String> roles = jwt.getClaimAsStringList("roles");

        if(microsoftId == null || microsoftId.isEmpty()){
            throw new InvalidInputException("oid not found in ID token");
        }
        if(email == null || email.isEmpty()){
            throw new InvalidInputException("email not found in ID token");
        }

        System.out.println("microsoftId: " + microsoftId);
        System.out.println("email: " + email);
        System.out.println("name: " + name);
        System.out.println("roles: " + roles);

        JwtUserInfoView jwtUserInfoView = authUserRepository.findStudentByUserUuid(microsoftId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        JwtUserInfo jwtUserInfo = JwtUserInfo.builder()
        .userId(jwtUserInfoView.getStudentId())
        .roles(roles)
        .build();

        String token = jwtService.generateToken(jwtUserInfo);

        return OAuthUserInfoDTO.builder()
        .microsoftId(microsoftId)
        .email(email)
        .name(name)
        .token(token)
        .build();
    }

}
