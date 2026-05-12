package com.tl_connect.dev.modules.application.projection;

import com.tl_connect.dev.shared.common.enums.ApplicationStatus;

public interface ApplicationAdminRow {
    Long getId();
    String getStudentCode();
    String getStudentName();
    String getApplicationTypeName();
    ApplicationStatus getStatus();
}
