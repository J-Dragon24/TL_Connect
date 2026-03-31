package com.tl_connect.dev.modules.payment.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateTuitionPaymentResDTO {
    private String orderUrl;
    private String transactionCode;
    private BigDecimal amount;
    private String invoiceStatus;
}
