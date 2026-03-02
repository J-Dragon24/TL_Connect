package com.tl_connect.dev.modules.exam.projection;

import java.time.LocalDate;
import java.time.LocalTime;

import com.tl_connect.dev.core.common.enums.AttendanceStatus;

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

}
