package com.tl_connect.dev.modules.payment.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl_connect.dev.core.common.enums.PaymentStatus;
import com.tl_connect.dev.core.common.enums.TuitionStatus;
import com.tl_connect.dev.core.common.enums.TypeTransaction;
import com.tl_connect.dev.core.common.exception.BadRequestException;
import com.tl_connect.dev.core.common.exception.NotFoundException;
import com.tl_connect.dev.modules.payment.PaymentRepository;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentReqDTO;
import com.tl_connect.dev.modules.payment.dto.CreateTuitionPaymentResDTO;
import com.tl_connect.dev.modules.payment.dto.ZaloPayOrderResultDTO;
import com.tl_connect.dev.modules.payment.entity.Payment;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoice;
import com.tl_connect.dev.modules.tuition.entity.TuitionInvoiceItem;
import com.tl_connect.dev.modules.tuition.entity.TuitionTransaction;
import com.tl_connect.dev.modules.tuition.projection.TuitionInvoiceView;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceItemRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TuitionInvoiceRepository invoiceRepository;
    private final TuitionInvoiceItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    private final TuitionTransactionRepository tuitionTransactionRepository;
    private final ObjectMapper objectMapper;
    private final ZaloPayService zaloPayService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public CreateTuitionPaymentResDTO createPayment(CreateTuitionPaymentReqDTO req) throws Exception {

        TuitionInvoiceView invoice = invoiceRepository.findByIdAndStudentId(req.getInvoiceId(), req.getStudentId())
            .orElseThrow(() -> new NotFoundException("Invoice not found"));

        if (invoice.getStatus() == TuitionStatus.PAID) {
            throw new BadRequestException("Invoice already paid");
        }

        if (invoice.getStatus() == TuitionStatus.CANCELLED) {
            throw new BadRequestException("Invoice is cancelled");
        }

        List<TuitionInvoiceItem> items = itemRepository.findAllByInvoiceId(req.getInvoiceId());


        if (items.isEmpty()) {
            throw new BadRequestException("Invoice has no items");
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
        
        ZaloPayOrderResultDTO  zaloResult = zaloPayService.createOrder(
            invoice.getFinalAmount().longValue(),
            String.valueOf(req.getStudentId()),
            description,
            itemJson
        );

        log.info("ZaloPay createOrder response: {}", zaloResult);

        Integer returnCode = (Integer) zaloResult.getRawResponse().get("return_code");
        if (returnCode == null || returnCode != 1) {
            throw new BadRequestException("ZaloPay error: "
                + zaloResult.getRawResponse().get("return_message")
                + " | sub_code: " + zaloResult.getRawResponse().get("sub_return_code")
                + " | sub_message: " + zaloResult.getRawResponse().get("sub_return_message"));
        }

        String transactionCode = zaloResult.getAppTransId();
        String orderUrl = zaloResult.getOrderUrl();
       
        Payment payment = new Payment();
        payment.setInvoiceId(req.getInvoiceId());
        payment.setAmount(invoice.getFinalAmount());
        payment.setProvider("ZALOPAY");
        payment.setTransactionCode(transactionCode);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        TuitionTransaction tx = new TuitionTransaction();
        tx.setStudentId(req.getStudentId());
        tx.setInvoiceId(req.getInvoiceId());
        tx.setAmount(invoice.getFinalAmount());
        tx.setType(TypeTransaction.PAYMENT);
        tx.setReferenceId(payment.getId());
        tx.setReferenceType("PAYMENT");
        tx.setDescription("Tạo lệnh thanh toán ZaloPay - " + transactionCode);
        tx.setCreatedAt(LocalDateTime.now());
        tuitionTransactionRepository.save(tx);

        return CreateTuitionPaymentResDTO.builder()
            .orderUrl(orderUrl)
            .transactionCode(transactionCode)
            .amount(invoice.getFinalAmount())
            .invoiceStatus(invoice.getStatus().name())
            .build();
    }

    @Transactional
    public void handleCallback(Map<String, String> callbackBody) throws Exception {

        // 1. Verify MAC từ ZaloPay
        String transId = zaloPayService.verifyCallback(callbackBody);

        // 2. Tìm payment theo transaction_code
        Payment payment = paymentRepository.findByTransactionCode(transId)
            .orElseThrow(() -> new NotFoundException("Payment not found: " + transId));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Callback duplicate, transId={} already SUCCESS", transId);
            return; // idempotent — bỏ qua nếu đã xử lý
        }

        String dataStr = callbackBody.get("data");
        JsonNode data = objectMapper.readTree(dataStr);
        long zptransid = data.get("zp_trans_id").asLong();
        int orderStatus = data.get("order_status").asInt();

        payment.setProviderTransId(zptransid);
        payment.setUpdatedAt(LocalDateTime.now());

        if (orderStatus != 1) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("Payment FAILED: transId={}, zptransid={}", transId, zptransid);
            return;
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        TuitionInvoice invoice = invoiceRepository.findById(payment.getInvoiceId())
            .orElseThrow(() -> new NotFoundException(
                "Invoice not found: " + payment.getInvoiceId()));

        invoice.setStatus(TuitionStatus.PAID);
        invoice.setUpdatedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        TuitionTransaction tx = new TuitionTransaction();
        tx.setStudentId(invoice.getStudentId());
        tx.setInvoiceId(invoice.getId());
        tx.setAmount(payment.getAmount());
        tx.setType(TypeTransaction.PAYMENT);
        tx.setReferenceId(payment.getId());
        tx.setReferenceType("PAYMENT");
        tx.setDescription("Thanh toán thành công ZaloPay - " + transId);
        tx.setCreatedAt(LocalDateTime.now());
        tuitionTransactionRepository.save(tx);

        log.info("Payment SUCCESS: invoiceId={}, transId={}", invoice.getId(), transId);
    }

    @Transactional
    public Map<String, Object> refund(String transCode) throws Exception {
        Payment payment = paymentRepository.findByTransactionCode(transCode)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Only SUCCESS payments can be refunded");
        }
        if (payment.getProviderTransId() == null) {
            throw new RuntimeException("No zptransid found for refund");
        }

        Map<String, Object> zaloResult = zaloPayService.refund(
                payment.getProviderTransId(),
                payment.getAmount().longValue(),
                payment.getTransactionCode()
        );

        Integer returnCode = (Integer) zaloResult.get("returncode");
        if (returnCode == null || returnCode != 1) {
            throw new RuntimeException("ZaloPay refund error: " + zaloResult.get("returnmessage"));
        }

        String mRefundId = zaloResult.containsKey("refundid")
        ? (String) zaloResult.get("refundid")
        : (String) zaloResult.get("mrefundid");
        if (mRefundId == null) {
            throw new RuntimeException("ZaloPay did not return refund id");
        }
        payment.setStatus(PaymentStatus.REFUND_PENDING);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        redisTemplate.opsForValue().set(
            "refund:" + payment.getId(),
            mRefundId,
            1,
            TimeUnit.HOURS
        );

        return zaloResult;
    }
}
