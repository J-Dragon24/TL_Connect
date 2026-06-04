package com.tl_connect.dev.modules.application.projection;

import java.time.LocalDateTime;

import com.tl_connect.dev.shared.common.enums.ApplicationStatus;

public interface ApplicationRow {
    Long getId();
    String getApplicationTypeName();
    ApplicationStatus getStatus();
    LocalDateTime getCreatedAt();
}
