package com.tl_connect.dev.modules.enroll.projection;

import java.time.LocalTime;

public interface CourseClassForEnrollRow {
    Long getId();
    String getLecturerCode();
    String getLecturerName();
    String getClassCode();
    String getClassName();
    Integer getCapacity();
    Integer getEnrolledCount();
    Integer getDayOfWeek();
    Integer getStartPeriod();
    Integer getEndPeriod();
    LocalTime getStartTime();
    LocalTime getEndTime();
    String getRoom();
}
