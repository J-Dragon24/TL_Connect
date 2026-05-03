package com.tl_connect.dev.modules.oauth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.oauth.entity.UserDevice;
import com.tl_connect.dev.modules.oauth.repository.UserDeviceRepository;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeviceService {
    private final UserDeviceRepository userDeviceRepository;

    @Transactional
    public void registerDevice(Long userId, String deviceId, String token, String platform) {

        Optional<UserDevice> byDevice = userDeviceRepository.findByDeviceId(deviceId);

        if (byDevice.isPresent()) {
            //  Case: cùng device (có thể token mới hoặc user mới)
            UserDevice d = byDevice.get();
            d.setOauthUserId(userId);
            d.setFcmToken(token);
            d.setPlatform(platform);
            d.setLastUsedAt(LocalDateTime.now());
            userDeviceRepository.save(d);

        } else {
            //  Case: device mới
            UserDevice d = new UserDevice();
            d.setOauthUserId(userId);
            d.setDeviceId(deviceId);
            d.setFcmToken(token);
            d.setPlatform(platform);
            userDeviceRepository.save(d);
        }
    }

    @Transactional
    public void removeDevice(String deviceId) {
        UserDevice byDevice = userDeviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new NotFoundException("Device not found"));
        userDeviceRepository.delete(byDevice);
    }
}
