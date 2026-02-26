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
public class DetailNotifyDTO {
    private String title;
    private String content;
    private String sender;
    private TargetType targetType;
    private LocalDateTime deadLine;
    private LocalDateTime createdAt;
}
