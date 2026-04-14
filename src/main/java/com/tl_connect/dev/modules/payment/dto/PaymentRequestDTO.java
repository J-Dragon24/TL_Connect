package com.tl_connect.dev.modules.payment.dto;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PaymentRequestDTO {
    private long amount;
    private String userId;
    private String description;

    // optional
    private String bankCode;
    private String language;
    private String ipAddress;

    // riêng ZaloPay
    private String itemJson;
}
