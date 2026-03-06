package com.tl_connect.dev.modules.schedule.projection;

import java.time.LocalTime;

public interface ScheduleRow {
    int getDayOfWeek();
    String getClassCode();
    String getSubjectName();
    String getSubjectCode();
    int getStartPeriod();
    int getEndPeriod();
    LocalTime getStartTime();
    LocalTime getEndTime();
    String getRoom();
    String getLecturerName();
    String getLecturerEmail();
    String getLecturerPhone();
    String getLecturerCode();
}
