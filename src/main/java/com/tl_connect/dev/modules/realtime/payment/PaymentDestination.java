package com.tl_connect.dev.modules.realtime.payment;

public class PaymentDestination {
    private PaymentDestination() {
    }

    public static final String PRIVATE = "/queue/payment";

    public static String paymentTopic(String transactionCode) {
        return "/topic/payment/" + transactionCode;
    }
}
