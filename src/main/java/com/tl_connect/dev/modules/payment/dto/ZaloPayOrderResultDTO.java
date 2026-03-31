package com.tl_connect.dev.modules.payment.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ZaloPayOrderResultDTO {
    private String appTransId;
    private String orderUrl;
    private String zpTransToken;
    private Map<String, Object> rawResponse;
}
