package com.tl_connect.dev.exam.projection;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tl_connect.dev.common.enums.AttendanceStatus;
import com.tl_connect.dev.common.enums.ExamStatus;

public interface ExamScheduleView {
    String getSubjectCode();

    String getSubjectName();

    String getClassCode();

    LocalDate getExamDate();

    LocalTime getStartTime();

    LocalTime getEndTime();

    String getExamRoom();

    String getExamLocation();

    String getExamFormat();

    String getExamType();

    Integer getExamAttempt();

    AttendanceStatus getAttendanceStatus();

    ExamStatus getExamStatus();
}
