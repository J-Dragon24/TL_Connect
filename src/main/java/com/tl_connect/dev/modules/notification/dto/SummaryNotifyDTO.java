package com.tl_connect.dev.modules.notification.dto;

import java.time.LocalDateTime;

import com.tl_connect.dev.core.common.enums.TargetType;

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
public class SummaryNotifyDTO {
    private Long id;
    private String title;
    private String sender;
    private Boolean isRead;
    private TargetType targetType;
    private LocalDateTime createdAt;
}
