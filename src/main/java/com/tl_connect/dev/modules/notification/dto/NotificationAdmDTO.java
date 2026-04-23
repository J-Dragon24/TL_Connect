package com.tl_connect.dev.modules.notification.dto;

import java.time.LocalDate;
import java.util.List;

import com.tl_connect.dev.core.common.enums.NotificationCreatedBy;
import com.tl_connect.dev.core.common.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationAdmDTO {
    private Long id;
    private String title;
    private String content;
    private NotificationCreatedBy createdBy;
    private NotificationType targetType;
    private List<Long> targetIds;
    private LocalDate deadLine;
    private Boolean isImportant;
}
