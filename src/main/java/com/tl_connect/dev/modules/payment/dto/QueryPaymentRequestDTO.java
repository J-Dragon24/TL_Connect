package com.tl_connect.dev.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryPaymentRequestDTO {
    @NotBlank(message = "Transaction code is required")
    private String transactionCode;
}
