package com.tl_connect.dev.modules.notification.projection;

import java.time.LocalDateTime;
import java.time.LocalDate;

import com.tl_connect.dev.core.common.enums.NotificationType;

public interface NotificationRow {
    Long getId();
    String getTitle();
    String getContent();
    String getCreatedBy();
    NotificationType getTargetType();
    LocalDateTime getCreatedAt();
    LocalDate getDeadLine();
    Boolean getIsRead();
}
