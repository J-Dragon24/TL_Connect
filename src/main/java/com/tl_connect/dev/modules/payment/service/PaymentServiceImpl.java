package com.tl_connect.dev.modules.payment.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl_connect.dev.modules.payment.PaymentRepository;
import com.tl_connect.dev.modules.payment.dto.CallbackPaymentDTO;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentReqDTO;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentResDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentResponseDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentResponseDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentResponseRawDTO;
import com.tl_connect.dev.modules.payment.dto.RefundInfoDTO;
import com.tl_connect.dev.modules.payment.dto.RefundRequestDTO;
import com.tl_connect.dev.modules.payment.dto.RefundResponseDTO;
import com.tl_connect.dev.modules.payment.entity.Payment;
import com.tl_connect.dev.modules.payment.provider.PaymentFactory;
import com.tl_connect.dev.modules.payment.provider.ProviderPayment;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoiceItem;
import com.tl_connect.dev.modules.tuition.entity.TuitionTransaction;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceView;
import com.tl_connect.dev.modules.tuition.service.interfaces.TuitionModifyService;
import com.tl_connect.dev.modules.tuition.service.interfaces.TuitionService;
import com.tl_connect.dev.shared.common.enums.PaymentStatus;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.enums.TuitionStatus;
import com.tl_connect.dev.shared.common.enums.TypeTransaction;
import com.tl_connect.dev.shared.common.exception.BadRequestException;
import com.tl_connect.dev.shared.common.exception.ConflictException;
import com.tl_connect.dev.shared.common.exception.ErrorException;
import com.tl_connect.dev.shared.common.exception.NotFoundException;
import com.tl_connect.dev.modules.payment.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final TuitionService tuitionService;
    private final PaymentRepository paymentRepository;
    private final TuitionModifyService tuitionModifyService;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final PaymentFactory paymentFactory;


    @Transactional
    public CreateTuitionPaymentResDTO createPayment(Long studentId, CreateTuitionPaymentReqDTO req) throws Exception {

        TuitionInvoiceView invoice = tuitionService.findByIdAndStudentId(req.getInvoiceId(), studentId);

        if (invoice.getStatus() == TuitionStatus.PAID) {
            throw new ConflictException("Invoice already paid");
        }

        if (invoice.getStatus() == TuitionStatus.CANCELLED) {
            throw new ConflictException("Invoice is cancelled");
        }

        List<TuitionInvoiceItem> items = tuitionService.findAllItemsByInvoiceId(req.getInvoiceId());

        if (items.isEmpty()) {
            throw new ErrorException(ResponseStatus.DATABASE_ERROR,"Invoice has no items");
        }

        List<Map<String, Object>> zaloItems = items.stream().map(item -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("itemid",     String.valueOf(item.getId()));
            m.put("itemname",   "Hoc phi lop " + item.getCourseClassId());
            m.put("itemprice",  item.getAmount().longValue());
            m.put("itemquantity", 1);
            return m;
        }).toList();

        String itemJson = objectMapper.writeValueAsString(zaloItems);

        String description = "Thanh toan hoc phi - Invoice #" + req.getInvoiceId();

        ProviderPayment providerPayment = paymentFactory.getProvider(req.getProvider());

        PaymentRequestDTO request = PaymentRequestDTO.builder()
            .amount(invoice.getFinalAmount().longValue())
            .userId(String.valueOf(studentId))
            .description(description)
            .language(req.getLanguage())
            .bankCode(req.getBankCode())
            .ipAddress(req.getIpAddress())
            .itemJson(itemJson)
            .build();
        
        PaymentResponseDTO paymentResponse = providerPayment.createPaymentUrl(request);

        log.info("{} createOrder response: {}", req.getProvider(), paymentResponse);

        if(paymentResponse.getTransactionId() == null) {
            throw new BadRequestException("Payment response is null");
        }

        String transactionCode = paymentResponse.getTransactionId();
        String orderUrl = paymentResponse.getPaymentUrl();
       
        Payment payment = Payment.create(req.getInvoiceId(), invoice.getFinalAmount(), req.getProvider().toUpperCase(), transactionCode);
        paymentRepository.save(payment);

        TuitionTransaction tx = TuitionTransaction.create(studentId, req.getInvoiceId(), invoice.getFinalAmount(), TypeTransaction.PAYMENT, payment.getId(), "PAYMENT", "Tạo lệnh thanh toán " + req.getProvider().toUpperCase() + " - " + transactionCode);
        tuitionModifyService.saveTuitionTransaction(tx);

        return CreateTuitionPaymentResDTO.builder()
            .orderUrl(orderUrl)
            .transactionCode(transactionCode)
            .amount(invoice.getFinalAmount())
            .invoiceStatus(invoice.getStatus().name())
            .build();
    }

    @Transactional
    public void handleCallback(Map<String, String> callbackBody) throws Exception {

        Payment payment = paymentRepository.findByTransactionCode(extractTxnRef(callbackBody))
            .orElseThrow(() -> new NotFoundException("Payment not found: " + extractTxnRef(callbackBody)));

        ProviderPayment providerPayment = paymentFactory.getProvider(payment.getProvider());

        CallbackPaymentDTO callback = providerPayment.callback(callbackBody);


        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Callback duplicate, transId={} already SUCCESS", callback.getTransactionId());
            return;
        }

        payment.setProviderTransId(callback.getProviderTransactionId());
        payment.setUpdatedAt(LocalDateTime.now());

        if (callback.getResponseCode() != 0) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("Payment FAILED: transId={}, providerTransId={}", callback.getTransactionId(), callback.getProviderTransactionId());
            return;
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        TuitionInvoice invoice = tuitionService.findById(payment.getInvoiceId());

        invoice.updateStatus(TuitionStatus.PAID);
        tuitionModifyService.saveTuition(invoice);

        TuitionTransaction tx = TuitionTransaction.create(invoice.getStudentId(), invoice.getId(), payment.getAmount(), TypeTransaction.PAYMENT, payment.getId(), "PAYMENT", "Thanh toán thành công " + payment.getProvider() + " - " + callback.getTransactionId());
        tuitionModifyService.saveTuitionTransaction(tx);

        log.info("Payment SUCCESS: invoiceId={}, transId={}", invoice.getId(), callback.getTransactionId());
    }

    @Transactional
    public RefundResponseDTO refund(RefundRequestDTO req) throws Exception {
        Payment payment = paymentRepository.findByTransactionCode(req.getTransactionCode())
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Only SUCCESS payments can be refunded");
        }
        if (payment.getProviderTransId() == null) {
            throw new RuntimeException("No zptransid found for refund");
        }

        RefundInfoDTO refundRequest = RefundInfoDTO.builder()
            .transactionId(payment.getTransactionCode())
            .amount(payment.getAmount().longValue())
            .transactionDate(payment.getCreatedAt()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
            .providerTransactionId(payment.getProviderTransId())
            .orderInfo(req.getOrderInfo())
            .createBy(req.getCreateBy())
            .ipAddress(req.getIpAddress())
            .type(req.getType())
            .build();

        ProviderPayment providerPayment = paymentFactory.getProvider(payment.getProvider());
        RefundResponseDTO refundResponse = providerPayment.refund(refundRequest);

        if (refundResponse.getResponseCode() != 0) {
            throw new RuntimeException(payment.getProvider() + " refund error: " + refundResponse.getMessage());
        }

        if (refundResponse.getRefundId() == null) {
            throw new RuntimeException(payment.getProvider() + " did not return refund id");
        }
        payment.setStatus(PaymentStatus.REFUND_PENDING);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        redisTemplate.opsForValue().set(
            "refund:" + payment.getId(),
            refundResponse.getRefundId(),
            1,
            TimeUnit.HOURS
        );

        refundResponse.setProvider(payment.getProvider());

        return refundResponse;
    }

    private String extractTxnRef(Map<String, String> callbackBody) {
        if(callbackBody.get("app_trans_id") != null) {
            return callbackBody.get("app_trans_id");
        }
        if(callbackBody.get("vnp_TxnRef") != null) {
            return callbackBody.get("vnp_TxnRef");
        }
        return null;
    }

    public QueryPaymentResponseDTO queryPaymentStatus(QueryPaymentRequestDTO req, String ipAddress) throws Exception {
        Payment payment = paymentRepository.findByTransactionCode(req.getTransactionCode())
            .orElseThrow(() -> new NotFoundException("Payment not found: " + req.getTransactionCode()));

        String transactionDate = payment.getCreatedAt()
            .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ProviderPayment providerPayment = paymentFactory.getProvider(payment.getProvider());
        QueryPaymentResponseRawDTO response = providerPayment.queryPaymentResult(req, ipAddress, transactionDate);

        log.info("Raw response from provider: {}", response.getRawData());

        return QueryPaymentResponseDTO.builder()
            .responseCode(response.getResponseCode())
            .message(response.getMessage())
            .transactionId(response.getTransactionId())
            .providerTransactionId(response.getProviderTransactionId())
            .amount(response.getAmount())
            .build();
    }
}
