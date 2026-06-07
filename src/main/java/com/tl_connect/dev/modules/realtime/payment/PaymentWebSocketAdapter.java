package com.tl_connect.dev.modules.realtime.payment;

import org.springframework.stereotype.Component;

import com.tl_connect.dev.modules.realtime.common.WebSocketGateway;
import com.tl_connect.dev.modules.realtime.payment.dto.PaymentStatusDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentWebSocketAdapter {
    private final WebSocketGateway gateway;

    public void sendPaymentUpdate(String transactionCode, PaymentStatusDTO dto) {
        gateway.send(PaymentDestination.paymentTopic(transactionCode), dto);
    }

    public void sendPrivatePayment(Long studentId, PaymentStatusDTO dto) {
        gateway.sendToUser(studentId, PaymentDestination.PRIVATE, dto);
    }
}
