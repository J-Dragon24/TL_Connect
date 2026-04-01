package com.tl_connect.dev.modules.exam.projection;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ExamScheduleAdminRow {
    Long getId();
    
    String getSubjectCode();

    String getSubjectName();

    LocalDate getExamDate();

    LocalTime getStartTime();

    LocalTime getEndTime();

    String getExamRoom();

    String getExamLocation();

    String getExamFormat();

    String getExamType();
}
