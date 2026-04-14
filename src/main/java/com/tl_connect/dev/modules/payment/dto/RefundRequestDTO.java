package com.tl_connect.dev.modules.payment.dto;

import com.tl_connect.dev.core.common.enums.RefundType;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundRequestDTO {
    @NotBlank(message = "Transaction code is required")
    private String transactionCode;  
    private String orderInfo;
    private String createBy;
    private String ipAddress;
    private RefundType type;  
}
