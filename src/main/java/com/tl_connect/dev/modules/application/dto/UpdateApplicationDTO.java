package com.tl_connect.dev.modules.application.dto;

import com.tl_connect.dev.shared.common.enums.ApplicationStatus;

import lombok.Data;

@Data
public class UpdateApplicationDTO {
    private ApplicationStatus status;
}
