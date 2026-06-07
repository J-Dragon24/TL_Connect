package com.tl_connect.dev.modules.realtime.notification.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tl_connect.dev.shared.common.enums.NotificationCreatedBy;
import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.common.enums.ReferenceTypeNotification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRealtimeDTO {
    private Long id;
    private String title;
    private String content;
    private NotificationCreatedBy createdBy;
    private NotificationType targetType;
    private Boolean isImportant;
    private ReferenceTypeNotification referenceType;
    private LocalDate deadLine;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
