package com.tl_connect.dev.modules.payment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tl_connect.dev.modules.payment.entity.Payment;
import com.tl_connect.dev.shared.common.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionCode(String transactionCode);

    List<Payment> findAllByStatus(PaymentStatus status);
}
