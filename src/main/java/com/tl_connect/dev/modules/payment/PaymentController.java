package com.tl_connect.dev.modules.payment;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentReqDTO;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentResDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentReturnRequest;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentResponseDTO;
import com.tl_connect.dev.modules.payment.dto.RefundRequestDTO;
import com.tl_connect.dev.modules.payment.dto.RefundResponseDTO;
import com.tl_connect.dev.modules.payment.service.PaymentService;
import com.tl_connect.dev.modules.tuition.service.TuitionModifyService;
import com.tl_connect.dev.shared.common.enums.PaymentStatus;
import com.tl_connect.dev.shared.common.enums.TuitionStatus;
import com.tl_connect.dev.shared.common.exception.UnauthorizeException;
import com.tl_connect.dev.shared.common.types.JwtUserInfo;
import com.tl_connect.dev.shared.common.ultility.ResponseHelper;
import com.tl_connect.dev.shared.config.VNPayConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final VNPayConfig vnpayConfig;
    private final TuitionModifyService tuitionModifyService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createTuitionPayment(Authentication authentication, @Valid @RequestBody CreateTuitionPaymentReqDTO req) throws Exception {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        CreateTuitionPaymentResDTO res = paymentService.createPayment(studentId, req);

        return ResponseHelper.success("Payment created successfully", res);
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
            return ResponseHelper.success("Payment callback success", null);
        } catch (Exception e) {
            return ResponseHelper.internalError("Payment callback failed");
        }
    }

    @GetMapping("/callback/vnpay")
    public ResponseEntity<?> handleVnPayCallback(@RequestParam Map<String, String> params) throws Exception {
        try {
            paymentService.handleCallback(params);
            return ResponseHelper.success("Payment callback success", null);
        } catch (Exception e) {
            return ResponseHelper.internalError("Payment callback failed");
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestBody @Valid RefundRequestDTO req) {
        try {
            RefundResponseDTO result = paymentService.refund(req);
            return ResponseHelper.success("Refund success", result);
        } catch (Exception e) {
            return ResponseHelper.internalError("Refund failed");
        }
    }

    @PostMapping("/get-status")
    public ResponseEntity<?> queryPaymentStatus(HttpServletRequest httpRequest, @RequestBody @Valid QueryPaymentRequestDTO req) {
        try {
            String vnp_IpAddr = vnpayConfig.getIpAddress(httpRequest);
            QueryPaymentResponseDTO result = paymentService.queryPaymentStatus(req, vnp_IpAddr);
            return ResponseHelper.success("Query payment status success", result);
        } catch (Exception e) {
            return ResponseHelper.internalError("Query payment status failed: " + e.getMessage());
        }
    }

    @PostMapping("/payment-return")
    public ResponseEntity<?> handlePaymentReturn(Authentication authentication, @RequestBody @Valid PaymentReturnRequest req) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserInfo userInfo)) {
            throw new UnauthorizeException("Authentication required");
        }
        Long studentId = userInfo.userId();
        tuitionModifyService.updateTuitionStatusByIdAndStudentId(req.getTuitionId(), studentId, TuitionStatus.PENDING);
        return ResponseHelper.success("Payment return success", null);
    }
}
