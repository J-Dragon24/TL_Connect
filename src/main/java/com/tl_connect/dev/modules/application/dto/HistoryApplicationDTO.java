package com.tl_connect.dev.modules.application.dto;

import java.time.LocalDateTime;

import com.tl_connect.dev.shared.common.enums.ApplicationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryApplicationDTO {
    private Long id;
    private String typeName;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
}
