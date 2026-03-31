package com.tl_connect.dev.modules.payment.service;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.springframework.stereotype.Service;


import com.crypto.HMACUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl_connect.dev.core.config.ZaloPayConfig;
import com.tl_connect.dev.modules.payment.dto.ZaloPayOrderResultDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZaloPayService{
    private final ZaloPayConfig zaloPayConfig;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    public ZaloPayOrderResultDTO createOrder(long amount, String userId, String description, String itemJson) throws Exception {

        String transId      = getCurrentTimeString("yyMMdd") + "_" + new Date().getTime();
        long   appTime      = System.currentTimeMillis();
        int appId        = zaloPayConfig.getAppId();

        Map<String, String> embeddata = new LinkedHashMap<>();
        embeddata.put("redirecturl",   zaloPayConfig.getRedirectUrl());
        embeddata.put("promotioninfo", "");
        embeddata.put("merchantinfo",  "eshop123");

        String embeddataJson = objectMapper.writeValueAsString(embeddata);

        String data = appId
        + "|" + transId
        + "|" + userId
        + "|" + amount
        + "|" + appTime
        + "|" + embeddataJson 
        + "|" + itemJson; 

        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, zaloPayConfig.getKey1(), data);

        Map<String, Object> order = new LinkedHashMap<>();
        order.put("app_id",       appId);
        order.put("app_trans_id", transId);
        order.put("app_user",     userId);
        order.put("amount",       amount);
        order.put("app_time",     appTime);
        order.put("embed_data",   embeddataJson);
        order.put("item",         itemJson);
        order.put("description",  description);
        order.put("callback_url", zaloPayConfig.getCallbackUrl());
        order.put("mac", mac);

        log.info("ZaloPay createOrder request: {}", order);


        MediaType mediaType = MediaType.parse("application/json");

        String jsonBody = objectMapper.writeValueAsString(order);

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
            .url(zaloPayConfig.getEndpointCreate())
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build();
        try (Response response = okHttpClient.newCall(request).execute();) {
            String responseBody = response.body().string();
            log.info("ZaloPay raw response: {}", responseBody);  // thêm dòng này
            log.info("ZaloPay response code: {}", response.code());  // thêm dòng này
            Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
            return ZaloPayOrderResultDTO.builder()
                .appTransId(transId)
                .orderUrl((String) result.get("order_url"))
                .zpTransToken((String) result.get("zp_trans_token"))
                .rawResponse(result)
                .build();
        }
    }

    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public String verifyCallback(Map<String, String> cbData) throws Exception {
        String mac = cbData.get("mac");
        String dataStr = cbData.get("data");

        String expectedMac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, zaloPayConfig.getKey2(), dataStr);
        if (!expectedMac.equals(mac)) {
            throw new SecurityException("Invalid MAC");
        }

        Map<String, Object> parsed = objectMapper.readValue(dataStr, Map.class);
        return (String) parsed.get("app_trans_id");
    }


    public Map<String, Object> refund(long zpTransId, long amount, String transCode) throws Exception {
        Random rand = new Random();
        long timestamp = System.currentTimeMillis(); // miliseconds
        String uid = timestamp + "" + (111 + rand.nextInt(888)); // unique id

        int appid = zaloPayConfig.getAppId();
        String mRefundId  = getCurrentTimeString("yyMMdd") + "_" + appid + "_" + uid;
        String description = "Hoàn tiền giao dịch " + transCode;

        Map<String, Object> order = new HashMap<String, Object>(){{
            put("app_id", appid);
            put("zp_trans_id", zpTransId);
            put("m_refund_id", mRefundId);
            put("timestamp", timestamp);
            put("amount", amount);
            put("description", description);
        }};

        // appid|zptransid|amount|description|timestamp
        String data = order.get("app_id") +"|"+ order.get("zp_trans_id") +"|"+ order.get("amount")
                +"|"+ order.get("description") +"|"+ order.get("timestamp");
        order.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, zaloPayConfig.getKey1(), data));

        MediaType mediaType = MediaType.parse("application/json");

        String jsonBody = objectMapper.writeValueAsString(order);

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
            .url(zaloPayConfig.getEndpointPartialRefund())
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new RuntimeException("Empty response from ZaloPay refund API");
            }
            String result = responseBody.string();
            return objectMapper.readValue(result, Map.class);

        }
    }

    public Map<String, Object> queryRefundStatus(String mRefundId) throws Exception {
        long timestamp = System.currentTimeMillis();
        String appId   = String.valueOf(zaloPayConfig.getAppId());

        // appid|mrefundid|timestamp
        String data = appId + "|" + mRefundId + "|" + timestamp;
        String mac  = HMACUtil.HMacHexStringEncode(
                HMACUtil.HMACSHA256, zaloPayConfig.getKey1(), data);

        Map<String, Object> params = new HashMap<>();
        params.put("app_id",     appId);
        params.put("m_refund_id", mRefundId);
        params.put("timestamp",  timestamp);
        params.put("mac", mac);

        String jsonBody = objectMapper.writeValueAsString(params);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url(zaloPayConfig.getEndpointQueryRefund())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new RuntimeException("Empty response from ZaloPay query refund API");
            }
            return objectMapper.readValue(responseBody.string(), Map.class);
        }
    }
}
