package com.tl_connect.dev.modules.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.tl_connect.dev.shared.common.enums.ApplicationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryDetailApplication {
    private String typeName;
    private ApplicationStatus status;
    private String content;
    private List<ApplicationAttachmentDTO> attachments;
    private LocalDateTime createdAt;
}
