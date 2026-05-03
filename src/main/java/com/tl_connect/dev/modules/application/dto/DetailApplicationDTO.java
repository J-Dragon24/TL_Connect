package com.tl_connect.dev.modules.application.dto;

import java.util.List;

import com.tl_connect.dev.shared.common.enums.ApplicationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailApplicationDTO {
    private Long id;
    private String studentCode;
    private String studentName;
    private String applicationTypeName;
    private ApplicationStatus status;
    private String content;
    private List<ApplicationAttachmentDTO> attachments;
}
