package com.tl_connect.dev.modules.payment.entity;

import com.tl_connect.dev.core.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "provider")
    private String provider;

    @Column(name = "transaction_code")
    private String transactionCode;

    @Column(name = "provider_trans_id")
    private String providerTransId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Payment create(Long invoiceId, BigDecimal amount, String provider, String transactionCode) {
        Payment payment = new Payment();
        payment.invoiceId = invoiceId;
        payment.amount = amount;
        payment.provider = provider;
        payment.transactionCode = transactionCode;
        payment.status = PaymentStatus.PENDING;
        return payment;
    }
}
