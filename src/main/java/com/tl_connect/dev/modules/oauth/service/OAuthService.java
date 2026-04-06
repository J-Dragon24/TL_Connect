package com.tl_connect.dev.modules.oauth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.core.common.enums.UserStatus;
import com.tl_connect.dev.core.common.exception.InvalidInputException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.core.common.types.JwtUserInfo;
import com.tl_connect.dev.core.common.types.UserInfo;
import com.tl_connect.dev.core.common.ultility.AuthHelper;
import com.tl_connect.dev.modules.oauth.dto.LoginRequestDTO;
import com.tl_connect.dev.modules.oauth.dto.OAuthUserInfoDTO;
import com.tl_connect.dev.modules.oauth.entity.OAuthUser;
import com.tl_connect.dev.modules.oauth.projection.JwtUserInfoView;
import com.tl_connect.dev.modules.oauth.repository.OAuthUserRepository;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final OAuthUserRepository oauthUserRepository;

    private final AuthHelper authHelper;

    private final JWTService jwtService;

    private final StudentRepository studentRepository;

    private final UserDeviceService userDeviceService;

    @Transactional
    public OAuthUserInfoDTO loginWithMicrosoft(LoginRequestDTO request) {

        UserInfo userInfo = authHelper.extractUserInfo(request.getAccessToken());

        String microsoftId = userInfo.oid();
        String email = userInfo.email();
        String name = userInfo.name();
        List<String> roles = userInfo.roles();
        String avatar = userInfo.avatar();

        if (microsoftId == null || microsoftId.isEmpty()) {
            throw new InvalidInputException("oid not found in ID token");
        }
        if (email == null || email.isEmpty()) {
            throw new InvalidInputException("email not found in ID token");
        }

        System.out.println("microsoftId: " + microsoftId);
        System.out.println("email: " + email);
        System.out.println("name: " + name);
        System.out.println("roles: " + roles);

        Optional<JwtUserInfoView> jwtUserInfoView = oauthUserRepository.findStudentByUserUuid(microsoftId);

        JwtUserInfo jwtUserInfo;

        if (jwtUserInfoView.isPresent()) {
            jwtUserInfo = JwtUserInfo.builder()
                    .userId(jwtUserInfoView.get().getStudentId())
                    .roles(roles)
                    .build();
        } else {
            String studentCode = email.split("@")[0];
            Student student = studentRepository.findByStudentCode(studentCode)
                    .orElseThrow(() -> new NotFoundException("Sinh viên không tồn tại trong hệ thống: " + studentCode));
            if (student.getOauthUserId() != null) {
                throw new InvalidInputException("Sinh viên đã được liên kết với tài khoản khác");
            }
            OAuthUser oauthUser = oauthUserRepository.save(OAuthUser.builder()
                    .userUuid(microsoftId)
                    .displayName(name)
                    .email(email)
                    .status(UserStatus.ACTIVE)
                    .build());
            student.setOauthUserId(oauthUser.getId());
            studentRepository.save(student);

            jwtUserInfo = JwtUserInfo.builder()
                    .userId(student.getId())
                    .roles(roles)
                    .build();
        }

        String token = jwtService.generateToken(jwtUserInfo);

        String devicePlatform = request.getPlatform() != null ? request.getPlatform().toLowerCase() : "unknown";

        userDeviceService.registerDevice(jwtUserInfoView.get().getOauthUserId(), request.getDeviceId(),
                request.getFcmToken(), devicePlatform);

        return OAuthUserInfoDTO.builder()
                .microsoftId(microsoftId)
                .email(email)
                .name(name)
                .token(token)
                .avatar(avatar)
                .build();
    }

}
