package com.tl_connect.dev.common.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseWrapper<T> {
    private int code;
    private String message;
    private T data;
    private Instant timestamp;
}
