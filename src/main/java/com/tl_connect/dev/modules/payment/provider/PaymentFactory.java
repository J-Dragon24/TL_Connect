package com.tl_connect.dev.modules.payment.provider;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentFactory {

    private final VnPayProvider vnPayService;
    private final ZaloPayProvider zaloPayService;

    public ProviderPayment getProvider(String provider) {
        return switch (provider.toUpperCase()) {
            case "VNPAY" -> vnPayService;
            case "ZALOPAY" -> zaloPayService;
            default -> throw new IllegalArgumentException("Unsupported payment provider");
        };
    }
}
