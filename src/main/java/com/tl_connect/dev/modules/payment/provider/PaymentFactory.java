package com.tl_connect.dev.modules.payment.provider;

import org.springframework.stereotype.Service;

import com.tl_connect.dev.shared.common.exception.InvalidInputException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentFactory {

    private final VnPayProvider vnPayProvider;
    private final ZaloPayProvider zaloPayProvider;
    private final MoMoProvider momoProvider;
    
    public ProviderPayment getProvider(String provider) {
        return switch (provider.toUpperCase()) {
            case "VNPAY" -> vnPayProvider;
            case "ZALOPAY" -> zaloPayProvider;
            case "MOMO" -> momoProvider;
            default -> throw new InvalidInputException("Unsupported payment provider");
        };
    }
}
