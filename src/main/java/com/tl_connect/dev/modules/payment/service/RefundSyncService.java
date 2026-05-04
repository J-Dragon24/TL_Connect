package com.tl_connect.dev.modules.payment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tl_connect.dev.modules.payment.PaymentRepository;
import com.tl_connect.dev.modules.payment.entity.Payment;
import com.tl_connect.dev.modules.payment.provider.ZaloPayProvider;
import com.tl_connect.dev.modules.tuition.entity.TuitionTransaction;
import com.tl_connect.dev.modules.tuition.repository.TuitionInvoiceRepository;
import com.tl_connect.dev.modules.tuition.repository.TuitionTransactionRepository;
import com.tl_connect.dev.shared.common.enums.PaymentStatus;
import com.tl_connect.dev.shared.common.enums.TuitionStatus;
import com.tl_connect.dev.shared.common.enums.TypeTransaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundSyncService {

    private final PaymentRepository paymentRepository;
    private final TuitionInvoiceRepository invoiceRepository;
    private final TuitionTransactionRepository tuitionTransactionRepository;
    private final ZaloPayProvider zaloPayService;
    private final StringRedisTemplate redisTemplate;

    // @Scheduled(fixedDelay = 30000) // 30 giây/lần
    public void syncRefundStatus() {
        List<Payment> pendingRefunds = paymentRepository.findAllByStatus(PaymentStatus.REFUND_PENDING);

        if (pendingRefunds.isEmpty()) return;

        log.info("Syncing refund status for {} payments", pendingRefunds.size());

        for (Payment payment : pendingRefunds) {
            String mRefundId = (String) redisTemplate.opsForValue().get("refund:" + payment.getId());

            if (mRefundId == null) {
                log.warn("mRefundId not found in Redis for paymentId={}, skipping", payment.getId());
                continue;
            }
            try {
                syncOne(payment, mRefundId);
            } catch (Exception e) {
                log.error("Failed to sync refund for mRefundId={}: {}",
                        mRefundId, e.getMessage());
            }
        }
    }

    @Transactional
    public void syncOne(Payment payment, String mRefundId) throws Exception {
        
        Map<String, Object> result = zaloPayService.queryRefundStatus(mRefundId);

        // refundstatus: 1=success, 2=failed, 3=processing
        Integer refundStatus = (Integer) result.get("refundstatus");
        if (refundStatus == null || refundStatus == 3) {
            log.debug("Refund still processing, mRefundId={}", mRefundId);
            return;
        }

        if (refundStatus == 1) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            invoiceRepository.findById(payment.getInvoiceId()).ifPresent(invoice -> {
                invoice.setStatus(TuitionStatus.CANCELLED);
                invoice.setUpdatedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);

                // Ghi transaction log
                TuitionTransaction tx = new TuitionTransaction();
                tx.setStudentId(invoice.getStudentId());
                tx.setInvoiceId(invoice.getId());
                tx.setAmount(payment.getAmount());
                tx.setType(TypeTransaction.REFUND);
                tx.setReferenceId(payment.getId());
                tx.setReferenceType("PAYMENT");
                tx.setDescription("Hoàn tiền ZaloPay - " + mRefundId);
                tx.setCreatedAt(LocalDateTime.now());
                tuitionTransactionRepository.save(tx);
            });

            redisTemplate.delete("refund:" + payment.getId());
            log.info("Refund SUCCESS: paymentId={}, mRefundId={}",
                    payment.getId(), mRefundId);

        } else if (refundStatus == 2) {
            // Hoàn tiền thất bại — rollback về SUCCESS
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            redisTemplate.delete("refund:" + payment.getId());
            log.warn("Refund FAILED: paymentId={}, mRefundId={}",
                    payment.getId(), mRefundId);
        }
    }
}