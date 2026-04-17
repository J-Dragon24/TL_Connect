package com.tl_connect.dev.modules.tuition.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.tl_connect.dev.core.common.enums.TypeTransaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tuition_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TuitionTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;
    
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeTransaction type;
    
    @Column(name = "reference_id", nullable = false)
    private Long referenceId;
    
    @Column(name = "reference_type", nullable = false)
    private String referenceType;

    @Column(name = "description", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static TuitionTransaction create(Long studentId, Long invoiceId, BigDecimal amount, TypeTransaction type, Long referenceId, String referenceType, String description) {
        TuitionTransaction tuitionTransaction = new TuitionTransaction();
        tuitionTransaction.studentId = studentId;
        tuitionTransaction.invoiceId = invoiceId;
        tuitionTransaction.amount = amount;
        tuitionTransaction.type = type;
        tuitionTransaction.referenceId = referenceId;
        tuitionTransaction.referenceType = referenceType;
        tuitionTransaction.description = description;
        tuitionTransaction.createdAt = LocalDateTime.now();
        return tuitionTransaction;
    }
}
