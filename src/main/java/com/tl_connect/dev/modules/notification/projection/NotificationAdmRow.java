package com.tl_connect.dev.modules.notification.projection;

import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.shared.common.enums.NotificationCreatedBy;
import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.common.enums.ReferenceTypeNotification;

public interface NotificationAdmRow {
    Long getId();
    String getTitle();
    String getContent();
    NotificationCreatedBy getCreatedBy();
    NotificationType getTargetType();
    List<Long> getTargetIds();
    LocalDate getDeadLine();
    Boolean getIsImportant();
    ReferenceTypeNotification getReferenceType();
}
