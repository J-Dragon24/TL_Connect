package com.tl_connect.dev.modules.payment.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDTO {
    private String provider;        // VNPAY | ZALOPAY
    private String transactionId;
    private String paymentUrl; 

    private Map<String, Object> rawData;    
}
