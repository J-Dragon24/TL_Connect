package com.tl_connect.dev.modules.enroll.projection;

public interface ScheduleIntervalRow {
    Long getClassScheduleId();
    Long getCourseClassId();
    String getClassCode();
    int getDayOfWeek();
    int getStartPeriod();
    int getEndPeriod();
}
