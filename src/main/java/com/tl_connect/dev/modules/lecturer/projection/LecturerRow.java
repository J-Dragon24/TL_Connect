package com.tl_connect.dev.modules.lecturer.projection;

import com.tl_connect.dev.core.common.enums.LecturerStatus;

public interface LecturerRow {
    Long getId();
    String getLecturerCode();
    String getFullName();
    String getEmail();
    String getPhoneNumber();
    String getDepartmentName();
    Boolean getIsAcademicAdvisor();
    LecturerStatus getStatus();
}
