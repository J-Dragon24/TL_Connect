package com.tl_connect.dev.modules.payment.dto;

import com.tl_connect.dev.shared.common.enums.RefundType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundInfoDTO {
    private String transactionId;          
    private Long amount;            
    private String transactionDate; 
    private String providerTransactionId;   
    private String orderInfo;
    private String createBy;
    private String ipAddress;
    private RefundType type;  
}
