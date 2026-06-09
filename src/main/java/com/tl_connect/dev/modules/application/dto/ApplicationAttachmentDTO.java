package com.tl_connect.dev.modules.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationAttachmentDTO {
    private Long id;
    private String fileKey;
    private String originalFilename;
    private Long fileSize;
    private String resourceType;
}
