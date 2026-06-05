package com.tl_connect.dev.modules.attendance.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.database.DatabaseException;
import com.tl_connect.dev.modules.attendance.dto.AttendanceRequest;
import com.tl_connect.dev.modules.attendance.entity.Attendance;
import com.tl_connect.dev.modules.attendance.repository.AttendanceRepository;
import com.tl_connect.dev.modules.attendance.service.interfaces.AttendanceService;
import com.tl_connect.dev.modules.attendance.service.interfaces.QrTokenService;
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.ultility.GeoUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final QrTokenService qrTokenService;

    @Override
    @Transactional
    public void checkIn(Long studentId, AttendanceRequest request) {

        Map<String, String> claims = qrTokenService.verify(request.getQrToken());

        Long classId = Long.parseLong(claims.get("classId"));
        String sessionId = claims.get("sessionId");

        validateGps(request.getLatitude(), request.getLongitude());

        boolean existed = attendanceRepository.existsBySessionIdAndStudentId(sessionId, studentId);
        if (existed) {
            throw new ConflictException("Student already checked in");
        }

        Attendance attendance = Attendance.create(classId, sessionId, studentId, LocalDateTime.now());
        try {
            attendanceRepository.save(attendance);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Failed to save attendance: " + e.getMessage());
        }
    }

    private void validateGps(Double lat, Double lng) {
        double schoolLat = 20.976006112646875;
        double schoolLng = 105.81562568381663;

        double distance = GeoUtil.calculateDistance(lat, lng, schoolLat, schoolLng);

        if (distance > 200) {
            throw new BadRequestException("You are outside the allowed attendance area");
        }
    }
}
