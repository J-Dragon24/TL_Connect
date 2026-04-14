package com.tl_connect.dev.modules.payment.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponseDTO {
    private String provider;
    private Integer responseCode;
    private String refundId;
    private String message;
    private String status;
    private Map<String, Object> rawData;
}
