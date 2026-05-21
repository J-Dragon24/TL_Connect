package com.tl_connect.dev.modules.oauth.service.interfaces;

import java.util.List;

public interface UserDeviceService {

    void registerDevice(Long userId, String deviceId, String token, String platform);

    void removeDevice(String deviceId);

    List<String> findTokensByUserIds(List<Long> targetIds);
}
