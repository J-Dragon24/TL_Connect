package com.tl_connect.dev.modules.attendance.projection;

public interface StudentAttendanceSummaryRow {
    String getStudentName();

    String getStudentCode();

    long getPresentCount();
}
