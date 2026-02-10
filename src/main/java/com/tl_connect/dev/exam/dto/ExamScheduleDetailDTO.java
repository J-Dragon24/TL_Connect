package com.tl_connect.dev.exam.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tl_connect.dev.common.enums.AttendanceStatus;
import com.tl_connect.dev.common.enums.ExamStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamScheduleDetailDTO {
    private String subjectCode;
    private String subjectName;
    private String classCode;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String examRoom;
    private String examLocation;
    private String examFormat;
    private String examType;
    private Integer examAttempt;
    private AttendanceStatus attendanceStatus;
    private ExamStatus examStatus;
}
