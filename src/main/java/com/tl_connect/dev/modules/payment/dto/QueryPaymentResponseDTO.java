package com.tl_connect.dev.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryPaymentResponseDTO {
    private Integer responseCode;
    private String message;
    private String transactionId;
    private String providerTransactionId;
    private Long amount;
}
