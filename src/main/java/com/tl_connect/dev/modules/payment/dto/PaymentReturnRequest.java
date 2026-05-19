package com.tl_connect.dev.modules.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReturnRequest {
    @NotNull(message = "Tuition ID is required")
    private Long tuitionId;
}
