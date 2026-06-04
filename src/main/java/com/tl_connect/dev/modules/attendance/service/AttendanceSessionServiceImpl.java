package com.tl_connect.dev.modules.attendance.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.attendance.service.interfaces.AttendanceSessionService;
import com.tl_connect.dev.modules.attendance.service.interfaces.QrTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceSessionServiceImpl implements AttendanceSessionService {

    private final QrTokenService qrTokenService;
    
    @Override
    public String openSession(Long courseClassId) {
        String sessionId = UUID.randomUUID().toString();
        String token = qrTokenService.generate(courseClassId, sessionId);
        return token;
    }
}
