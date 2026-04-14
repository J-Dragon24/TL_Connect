package com.tl_connect.dev.modules.payment.provider;

import java.util.Map;

import com.tl_connect.dev.modules.payment.dto.CallbackPaymentDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentResponseDTO;

public interface ProviderPayment {
    PaymentResponseDTO createPaymentUrl(PaymentRequestDTO request) throws Exception;
    
    CallbackPaymentDTO callback(Map<String, String> params) throws Exception;
}
