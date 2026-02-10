package com.tl_connect.dev.notification.projection;

import java.time.LocalDateTime;

import com.tl_connect.dev.common.enums.TargetType;

public interface NotificationRow {
    Long getId();
    String getTitle();
    String getSender();
    Boolean getIsRead();
    TargetType getTargetType();
    LocalDateTime getCreatedAt();
}
