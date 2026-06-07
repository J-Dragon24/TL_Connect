package com.tl_connect.dev.modules.realtime.payment;

import com.tl_connect.dev.modules.realtime.payment.dto.PaymentStatusDTO;

public interface PaymentGateway {

    void send(PaymentStatusDTO dto);
}
