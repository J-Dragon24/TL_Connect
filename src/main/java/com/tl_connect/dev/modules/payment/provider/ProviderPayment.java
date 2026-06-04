package com.tl_connect.dev.modules.payment.provider;

import java.util.Map;

import com.tl_connect.dev.modules.payment.dto.CallbackPaymentDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentResponseDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentResponseRawDTO;
import com.tl_connect.dev.modules.payment.dto.RefundInfoDTO;
import com.tl_connect.dev.modules.payment.dto.RefundResponseDTO;

public interface ProviderPayment {    
    PaymentResponseDTO createPaymentUrl(PaymentRequestDTO request) throws Exception;
    
    CallbackPaymentDTO callback(Map<String, String> params) throws Exception;

    RefundResponseDTO refund(RefundInfoDTO request) throws Exception;

    QueryPaymentResponseRawDTO queryPaymentResult(QueryPaymentRequestDTO request, String ipAddress, String transactionDate) throws Exception;
}
