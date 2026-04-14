package com.tl_connect.dev.modules.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTuitionPaymentReqDTO {
    @NotNull(message = "Invoice ID is required")
    Long invoiceId;

    @NotNull(message = "Provider is required")
    String provider;

    String language;

    String bankCode;

    String ipAddress;
}
