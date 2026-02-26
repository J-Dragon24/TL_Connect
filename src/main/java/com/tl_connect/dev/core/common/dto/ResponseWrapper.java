package com.tl_connect.dev.core.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseWrapper<T> {
    private int code;
    private String message;
    private T data;
}
