package com.tl_connect.dev.modules.attendance.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.modules.attendance.dto.ClassAttendanceSummaryResponse;
import com.tl_connect.dev.modules.attendance.dto.StudentAttendanceSummary;
import com.tl_connect.dev.modules.attendance.projection.ClassAttendanceSummaryView;
import com.tl_connect.dev.modules.attendance.projection.StudentAttendanceSummaryRow;
import com.tl_connect.dev.modules.attendance.repository.AttendanceRepository;
import com.tl_connect.dev.modules.attendance.service.interfaces.AttendanceStatisticsService;
import com.tl_connect.dev.shared.common.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceStatisticsServiceImpl implements AttendanceStatisticsService {

    private final AttendanceRepository attendanceRepository;

    @Override
    public ClassAttendanceSummaryResponse getClassSummary(Long courseClassId) {
        ClassAttendanceSummaryView summary = attendanceRepository.getClassAttendanceSummary(courseClassId)
                .orElseThrow(() -> new NotFoundException("Attendance not found"));
        
        List<StudentAttendanceSummaryRow> studentAttendances = attendanceRepository.getStudentAttendanceSummary(courseClassId);

        List<StudentAttendanceSummary> studentSummaries = new ArrayList<>();
        for (StudentAttendanceSummaryRow studentAttendance : studentAttendances) {
            long absentCount = summary.getTotalSessions() - studentAttendance.getPresentCount();
            double attendanceRate = summary.getTotalSessions() == 0 ? 0 : (studentAttendance.getPresentCount() * 100.0) / summary.getTotalSessions();
            StudentAttendanceSummary studentSummary = StudentAttendanceSummary.builder()
                .studentCode(studentAttendance.getStudentCode())
                .studentName(studentAttendance.getStudentName())
                .presentCount(studentAttendance.getPresentCount())
                .absentCount(absentCount)
                .attendanceRate(attendanceRate)
                .build();
            studentSummaries.add(studentSummary);
        };

        return ClassAttendanceSummaryResponse.builder()
            .classCode(summary.getClassCode())
            .className(summary.getClassName())
            .totalSessions(summary.getTotalSessions())
            .students(studentSummaries)
            .build();
    }
}
