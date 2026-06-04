package com.tl_connect.dev.modules.payment.provider;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crypto.HMACUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl_connect.dev.modules.payment.dto.CallbackPaymentDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.PaymentResponseDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentRequestDTO;
import com.tl_connect.dev.modules.payment.dto.QueryPaymentResponseRawDTO;
import com.tl_connect.dev.modules.payment.dto.RefundInfoDTO;
import com.tl_connect.dev.modules.payment.dto.RefundResponseDTO;
import com.tl_connect.dev.shared.common.exception.ExternalException;
import com.tl_connect.dev.shared.config.MoMoConfig;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@RequiredArgsConstructor
public class MoMoProvider implements ProviderPayment {

    private final MoMoConfig config;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    @Override
    public PaymentResponseDTO createPaymentUrl(PaymentRequestDTO _request) throws Exception {
        try{
            String orderId = String.valueOf(System.currentTimeMillis());
            String requestId = String.valueOf(System.currentTimeMillis());
            String requestType = "captureWallet";

            String extraData = "";

            Map<String, String> momoParams = new LinkedHashMap<>();
            momoParams.put("accessKey", config.getAccessKey());
            momoParams.put("amount", String.valueOf(_request.getAmount()));
            momoParams.put("extraData", extraData);
            momoParams.put("ipnUrl", config.getIpnUrl());
            momoParams.put("orderId", orderId);
            momoParams.put("orderInfo", _request.getDescription() != null ? _request.getDescription() : "Thanh toan don hang:" + orderId);
            momoParams.put("partnerCode", config.getPartnerCode());
            momoParams.put("redirectUrl", config.getRedirectUrl());
            momoParams.put("requestId", requestId);
            momoParams.put("requestType", requestType);

            StringBuilder rawHash = new StringBuilder();

            boolean first = true;

            for (Map.Entry<String, String> entry : momoParams.entrySet()) {

                if (!first) {
                    rawHash.append("&");
                }

                rawHash.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());

                first = false;
            }


            String signature = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, config.getSecretKey(), rawHash.toString());

            
            Map<String, Object> body = new HashMap<>();

            body.put("partnerCode", config.getPartnerCode());
            body.put("requestId", requestId);
            body.put("amount", _request.getAmount());
            body.put("orderId", orderId);
            body.put("orderInfo", _request.getDescription() != null ? _request.getDescription() : "Thanh toan don hang:" + orderId);
            body.put("redirectUrl", config.getRedirectUrl());
            body.put("ipnUrl", config.getIpnUrl());
            body.put("lang", "vi");
            body.put("requestType", requestType);
            body.put("autoCapture", true);
            body.put("extraData", extraData);
            body.put("signature", signature);

            String json = objectMapper.writeValueAsString(body);

            Request request = new Request.Builder()
                .url(config.getEndpoint() + config.getCreate())
                .post(RequestBody.create(json, MediaType.get("application/json")))
                .build();

            try (Response response = okHttpClient.newCall(request).execute()) {

                String resBody = response.body().string();

                Map<String, Object> result = objectMapper.readValue(resBody, Map.class);

                System.out.println("result: " + result);

                Integer resultCode = (Integer) result.get("resultCode");

                if (resultCode != 0) {
                    throw new ExternalException("MoMo payment failed: " + result.get("message"));
                }

                return PaymentResponseDTO.builder()
                        .provider("MOMO")
                        .transactionId(orderId)
                        .paymentUrl((String) result.get("payUrl"))
                        .build();
            }
        } catch (Exception e) {
            throw new ExternalException("Create MoMo payment failed: " + e.getMessage());
        }
    }

    @Override
    public CallbackPaymentDTO callback(Map<String, String> params) throws Exception {
        String partnerCode = (String) params.get("partnerCode");

        String orderId = (String) params.get("orderId");

        String requestId = (String) params.get("requestId");

        String orderInfo = (String) params.get("orderInfo");

        String orderType = (String) params.get("orderType");

        String transId = String.valueOf(params.get("transId"));

        String message = (String) params.get("message");

        String payType = (String) params.get("payType");

        String responseTime = String.valueOf(params.get("responseTime"));

        String extraData = (String) params.get("extraData");

        Long amount = Long.valueOf((String)params.get("amount"));

        Integer resultCode = Integer.valueOf((String) params.get("resultCode"));

        String momoSignature = (String) params.get("signature");

        String rawHash =
                "accessKey=" + config.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&message=" + message +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&orderType=" + orderType +
                "&partnerCode=" + partnerCode +
                "&payType=" + payType +
                "&requestId=" + requestId +
                "&responseTime=" + responseTime +
                "&resultCode=" + resultCode +
                "&transId=" + transId;

        String signature = HMACUtil.HMacHexStringEncode(
            HMACUtil.HMACSHA256,
            config.getSecretKey(),
            rawHash
        );

        if (!signature.equals(momoSignature)) {
            throw new ExternalException("Invalid MoMo signature");
        }

        if (resultCode == 0) {
            return CallbackPaymentDTO.builder()
                    .responseCode(0)
                    .transactionId(orderId)
                    .providerTransactionId(transId)
                    .build();
        }

        return CallbackPaymentDTO.builder()
            .responseCode(-1)
            .transactionId(orderId)
            .providerTransactionId(transId)
            .build();
    }

    @Override
    public RefundResponseDTO refund(RefundInfoDTO request) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refund'");
    }

    @Override
    public QueryPaymentResponseRawDTO queryPaymentResult(QueryPaymentRequestDTO request, String orderInfo, String transactionId) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryPaymentResult'");
    }
    
}
