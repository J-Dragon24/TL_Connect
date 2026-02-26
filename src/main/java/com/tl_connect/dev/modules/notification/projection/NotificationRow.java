package com.tl_connect.dev.modules.notification.projection;

import java.time.LocalDateTime;

import com.tl_connect.dev.core.common.enums.TargetType;

public interface NotificationRow {
    Long getId();
    String getTitle();
    String getSender();
    TargetType getTargetType();
    LocalDateTime getCreatedAt();
    LocalDateTime getDeadLine();
}
