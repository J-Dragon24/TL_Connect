package com.tl_connect.dev.modules.application.projection;

import java.time.LocalDateTime;

import com.tl_connect.dev.shared.common.enums.ApplicationStatus;

public interface DetailApplicationView {
    String getApplicationTypeName();
    ApplicationStatus getStatus();
    String getContent();
    LocalDateTime getCreatedAt();
}
