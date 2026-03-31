package com.tl_connect.dev.modules.payment;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.core.common.ultility.ResponseHelper;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentReqDTO;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentResDTO;
import com.tl_connect.dev.modules.payment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createTuitionPayment(@Valid @RequestBody CreateTuitionPaymentReqDTO req) throws Exception {

        CreateTuitionPaymentResDTO res = paymentService.createPayment(req);

        return ResponseHelper.success("Tạo đơn thanh toán thành công", res);
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handleTuitionCallback(@RequestBody Map<String, String> callbackBody) throws Exception {
        try {
            paymentService.handleCallback(callbackBody);
            return ResponseHelper.success("Thanh toán thành công", null);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestParam String transCode) {
        try {
            Map<String, Object> result = paymentService.refund(transCode);
            return ResponseHelper.success("Hoàn tiền thành công", result);
        } catch (Exception e) {
            return ResponseHelper.internalError(e.getMessage());
        }
    }
}
