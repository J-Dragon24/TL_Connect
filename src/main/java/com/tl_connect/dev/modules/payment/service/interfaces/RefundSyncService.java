package com.tl_connect.dev.modules.payment.service.interfaces;

import com.tl_connect.dev.modules.payment.entity.Payment;

public interface RefundSyncService {

    void syncRefundStatus();

    void syncOne(Payment payment, String mRefundId) throws Exception;
}
