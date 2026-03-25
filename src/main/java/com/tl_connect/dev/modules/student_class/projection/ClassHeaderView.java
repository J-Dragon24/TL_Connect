package com.tl_connect.dev.modules.student_class.projection;

public interface ClassHeaderView {
    Long getClassId();
    String getClassCode();
    Integer getStartYear();
    String getMajorName();
    String getLecturerCode();
    String getAcademicAdvisor();
    String getPhoneNumber();
    String getEmail();
}
