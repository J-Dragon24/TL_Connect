package com.tl_connect.dev.modules.notification.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.tl_connect.dev.core.common.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailNotifyDTO {
    private String title;
    private String content;
    private String createdBy;
    private NotificationType targetType;
    private LocalDate deadLine;
    private LocalDateTime createdAt;
}
