package com.tl_connect.dev.modules.lecturer.projection;

import com.tl_connect.dev.core.common.enums.LecturerStatus;

public interface AcademicAdvisorDetailView {
    Long getLecturerId();
    String getLecturerCode();
    String getLecturerName();
    String getLecturerEmail();
    String getLecturerPhoneNumber();
    String getDepartmentCode();
    LecturerStatus getLecturerStatus();
    String getStudentClassCode();
    String getClassMajorCode();
    Integer getStudentClassYear();
}
