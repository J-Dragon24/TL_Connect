package com.tl_connect.dev.modules.oauth.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.tl_connect.dev.modules.oauth.dto.LoginRequestDTO;
import com.tl_connect.dev.modules.oauth.dto.OAuthUserInfoDTO;
import com.tl_connect.dev.modules.oauth.entity.OAuthUser;
import com.tl_connect.dev.modules.oauth.projection.JwtUserInfoView;
import com.tl_connect.dev.modules.oauth.repository.OAuthUserRepository;
import com.tl_connect.dev.modules.student.entity.Student;
import com.tl_connect.dev.modules.student.service.interfaces.StudentService;
import com.tl_connect.dev.shared.common.exception.ForbiddenException;
import com.tl_connect.dev.shared.common.exception.InvalidInputException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.shared.types.JwtUserInfo;
import com.tl_connect.dev.shared.types.UserInfo;
import com.tl_connect.dev.shared.ultility.AuthHelper;
import com.tl_connect.dev.shared.ultility.TokenHelper;
import com.tl_connect.dev.modules.oauth.service.interfaces.JWTService;
import com.tl_connect.dev.modules.oauth.service.interfaces.OAuthService;
import com.tl_connect.dev.modules.oauth.service.interfaces.RefreshTokenService;
import com.tl_connect.dev.modules.oauth.service.interfaces.UserDeviceService;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
    private final OAuthUserRepository oauthUserRepository;

    private final AuthHelper authHelper;

    private final JWTService jwtService;

    private final StudentService studentService;

    private final UserDeviceService userDeviceService;
    
    private final RefreshTokenService refreshTokenService;
    
    @Transactional
    public OAuthUserInfoDTO loginWithMicrosoft(LoginRequestDTO request) {
        UserInfo userInfo = authHelper.extractUserInfo(request.getAccessToken());

        String microsoftId = userInfo.oid();
        String email = userInfo.email();
        String name = userInfo.name();
        List<String> roles = userInfo.roles();

        // String microsoftId = "1deb00a9-835c-4ab7-a50f-57c12a56c7bd";
        // String email = "nhokthanh3211@gmail.com";
        // String name = "Nguyen Van A";
        // List<String> roles = Arrays.asList("ADMIN");

        if (microsoftId == null || microsoftId.isEmpty()) {
            throw new InvalidInputException("Oid not found in ID token");
        }
        if (email == null || email.isEmpty()) {
            throw new InvalidInputException("Email not found in ID token");
        }

        JwtUserInfo jwtUserInfo;
        if(roles.contains("STUDENT")) {
            jwtUserInfo = processStudentLogin(microsoftId, email, name, roles, request.getDeviceId(), request.getFcmToken(), request.getPlatform());
        } else if(roles.contains("ADMIN")||roles.contains("LECTURER")||roles.contains("STAFF")) {
            jwtUserInfo = processAdminLogin(microsoftId, email, name, roles);
        } else {
            throw new ForbiddenException("Access denied");
        }

        String accessToken = jwtService.generateToken(jwtUserInfo);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", jwtUserInfo.userId());
        data.put("roles", roles);

        String refreshToken = TokenHelper.generateRefreshToken();
        refreshTokenService.save(refreshToken, data);

        return OAuthUserInfoDTO.builder()
                .microsoftId(microsoftId)
                .email(email)
                .name(name)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private JwtUserInfo processStudentLogin(String microsoftId, String email, String name, List<String> roles, String deviceId, String fcmToken, String platform) {

        if(deviceId == null || deviceId.isEmpty() || fcmToken == null || fcmToken.isEmpty()) {
            throw new InvalidInputException("Device ID and FCM token are required");
        }

        Optional<JwtUserInfoView> jwtUserInfoView = oauthUserRepository.findStudentByUserUuid(microsoftId);

        JwtUserInfo jwtUserInfo;

        if (jwtUserInfoView.isPresent()) {
            jwtUserInfo = JwtUserInfo.builder()
                    .userId(jwtUserInfoView.get().getStudentId())
                    .oauthUserId(jwtUserInfoView.get().getOauthUserId())
                    .roles(roles)
                    .build();
        } else {
            String studentCode = email.split("@")[0].toUpperCase();
            Student student = studentService.findByStudentCode(studentCode);
            if (student.getOauthUserId() != null) {
                throw new InvalidInputException("Student is already linked to another account");
            }
            OAuthUser oauthUser = oauthUserRepository.save(OAuthUser.create(microsoftId, name, email));
            student.setOauthUserId(oauthUser.getId());
            studentService.save(student);

            jwtUserInfo = JwtUserInfo.builder()
                    .userId(student.getId())
                    .oauthUserId(oauthUser.getId())
                    .roles(roles)
                    .build();
        }

        String devicePlatform = platform != null ? platform.toLowerCase() : "unknown";

        userDeviceService.registerDevice(jwtUserInfo.oauthUserId(), deviceId, fcmToken, devicePlatform);

        return jwtUserInfo;
    }

    private JwtUserInfo processAdminLogin(String microsoftId, String email, String name, List<String> roles) {
        JwtUserInfo jwtUserInfo;

        Optional<OAuthUser> oauthUser = oauthUserRepository.findByUserUuid(microsoftId);

        if (oauthUser.isPresent()) {
            jwtUserInfo = JwtUserInfo.builder()
                    .userId(oauthUser.get().getId())
                    .oauthUserId(oauthUser.get().getId())
                    .roles(roles)
                    .build();
        } else {
            throw new NotFoundException("Account not found: " + microsoftId);
        }


        return jwtUserInfo;
    }


    public OAuthUser findById(Long oauthUserId){
        return oauthUserRepository.findById(oauthUserId)
            .orElseThrow(() -> new NotFoundException("Oauth user not found"));
    }

}
