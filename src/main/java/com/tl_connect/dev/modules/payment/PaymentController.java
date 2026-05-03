package com.tl_connect.dev.modules.payment;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentReqDTO;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentResDTO;
import com.tl_connect.dev.modules.payment.dto.RefundRequestDTO;
import com.tl_connect.dev.modules.payment.dto.RefundResponseDTO;
import com.tl_connect.dev.modules.payment.service.PaymentService;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createTuitionPayment(Authentication authentication, @Valid @RequestBody CreateTuitionPaymentReqDTO req) throws Exception {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        CreateTuitionPaymentResDTO res = paymentService.createPayment(studentId, req);

        return ResponseHelper.success("Tạo đơn thanh toán thành công", res);
    }

    @PostMapping("/callback/zalopay")
    public ResponseEntity<?> handleZaloPayCallback(@RequestBody Map<String, Object> callbackBody) throws Exception {
        try {
            Map<String, String> params = callbackBody.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> String.valueOf(e.getValue())
                ));
            paymentService.handleCallback(params);
            return ResponseHelper.success("Thanh toán thành công", null);
        } catch (Exception e) {
            return ResponseHelper.internalError("Thanh toán thất bại");
        }
    }

    @GetMapping("/callback/vnpay")
    public ResponseEntity<?> handleVnPayCallback(@RequestParam Map<String, String> params) throws Exception {
        try {
            paymentService.handleCallback(params);
            return ResponseHelper.success("Thanh toán thành công", null);
        } catch (Exception e) {
            return ResponseHelper.internalError("Thanh toán thất bại");
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestBody @Valid RefundRequestDTO req) {
        try {
            RefundResponseDTO result = paymentService.refund(req);
            return ResponseHelper.success("Hoàn tiền thành công", result);
        } catch (Exception e) {
            return ResponseHelper.internalError("Hoàn tiền thất bại");
        }
    }
}
