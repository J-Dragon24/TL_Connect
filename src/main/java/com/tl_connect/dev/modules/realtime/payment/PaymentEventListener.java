package com.tl_connect.dev.modules.realtime.payment;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tl_connect.dev.modules.realtime.payment.dto.PaymentStatusDTO;
import com.tl_connect.dev.modules.realtime.payment.dto.PaymentSuccessEvent;
import com.tl_connect.dev.shared.common.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentGateway paymentGateway;

    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT
    )
    public void handlePaymentSuccess(PaymentSuccessEvent event) {

        PaymentStatusDTO dto = PaymentStatusDTO.builder()
                .tuitionId(event.getTuitionId())
                .transactionCode(event.getTransactionCode())
                .status(PaymentStatus.SUCCESS)
                .build();
        
        paymentGateway.send(dto);
    }
}
