package com.tl_connect.dev.modules.notification.projection;

import java.time.LocalDateTime;

import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.common.enums.ReferenceTypeNotification;

import java.time.LocalDate;

public interface NotificationRow {
    Long getId();
    String getTitle();
    String getContent();
    String getCreatedBy();
    NotificationType getTargetType();
    Boolean getIsImportant();
    ReferenceTypeNotification getReferenceType();
    LocalDateTime getCreatedAt();
    LocalDate getDeadLine();
    Boolean getIsRead();
}
