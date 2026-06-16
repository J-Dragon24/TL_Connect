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
    @Override
    public boolean existsBySessionIdAndStudentId(String sessionId, Long studentId) {
        return attendanceRepository.existsBySessionIdAndStudentId(sessionId, studentId);
    }

    @Override
    public void save(Attendance attendance) {
        try {
            attendanceRepository.save(attendance);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Failed to save attendance: " + e.getMessage());
        }
    }
}
