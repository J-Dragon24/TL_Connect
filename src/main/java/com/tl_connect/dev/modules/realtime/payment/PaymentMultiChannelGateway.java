package com.tl_connect.dev.modules.realtime.payment;

import org.springframework.stereotype.Component;

import com.tl_connect.dev.modules.realtime.payment.dto.PaymentStatusDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentMultiChannelGateway implements PaymentGateway {

    private final PaymentWebSocketAdapter websocketAdapter;

    @Override
    public void send(PaymentStatusDTO dto) {
        websocketAdapter.sendPaymentUpdate(dto.getTransactionCode(), dto);
    }
}
