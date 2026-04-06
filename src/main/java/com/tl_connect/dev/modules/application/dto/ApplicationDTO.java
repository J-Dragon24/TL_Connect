package com.tl_connect.dev.modules.application.dto;

import com.tl_connect.dev.core.common.enums.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {
    private Long id;
    private String studentCode;
    private String studentName;
    private String applicationTypeName;
    private ApplicationStatus status;
}
