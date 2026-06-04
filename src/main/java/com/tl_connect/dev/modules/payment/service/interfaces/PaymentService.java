package com.tl_connect.dev.modules.payment.service.interfaces;

import java.util.Map;

import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentReqDTO;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentResDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentResponseDTO;
import com.tl_connect.dev.modules.payment.dto.RefundRequestDTO;
import com.tl_connect.dev.modules.payment.dto.RefundResponseDTO;

public interface PaymentService {

    CreateTuitionPaymentResDTO createPayment(Long studentId, CreateTuitionPaymentReqDTO req) throws Exception;

    void handleCallback(Map<String, String> callbackBody) throws Exception;

    RefundResponseDTO refund(RefundRequestDTO req) throws Exception;

    QueryPaymentResponseDTO queryPaymentStatus(QueryPaymentRequestDTO req, String ipAddress) throws Exception;
}
