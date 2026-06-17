package com.tl_connect.dev.modules.enroll.projection;

public interface DetailsForCheckEnrollRow {
    String getClassCode();
    Long getSemesterId();
    Long getSubjectId();
    Long getClassScheduleId();
    Integer getDayOfWeek();
    Integer getStartPeriod();
    Integer getEndPeriod();
    Integer getCredits();
}
