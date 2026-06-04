package com.tl_connect.dev.modules.attendance.service.interfaces;

import java.util.Map;

public interface QrTokenService {
    String generate(
            Long classId,
            String sessionId
    );

    Map<String, String> verify(String token);
}
