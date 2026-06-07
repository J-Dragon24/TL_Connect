package com.tl_connect.dev.modules.realtime.payment.dto;

import com.tl_connect.dev.shared.common.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentStatusDTO {
    private Long tuitionId;
    private String transactionCode;
    private PaymentStatus status;
}
