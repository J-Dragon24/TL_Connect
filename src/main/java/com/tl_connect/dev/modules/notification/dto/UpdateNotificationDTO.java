package com.tl_connect.dev.modules.notification.dto;

import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.shared.common.enums.NotificationCreatedBy;
import com.tl_connect.dev.shared.common.enums.NotificationType;
import com.tl_connect.dev.shared.common.enums.ReferenceTypeNotification;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationDTO {
    @Size(min = 1, message = "Title is required")
    private String title;
    @Size(min = 1, message = "Content is required")
    private String content;
    private NotificationCreatedBy createdBy;
    private NotificationType targetType;
    private List<Long> targetIds;
    private Boolean isImportant;
    private LocalDate deadLine;
    private ReferenceTypeNotification referenceType;
}
