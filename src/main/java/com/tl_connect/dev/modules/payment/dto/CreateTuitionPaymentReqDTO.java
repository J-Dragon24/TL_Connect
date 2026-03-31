package com.tl_connect.dev.modules.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTuitionPaymentReqDTO {
    @NotNull(message = "Student ID is required")
    Long studentId;
    @NotNull(message = "Invoice ID is required")
    Long invoiceId;
}
