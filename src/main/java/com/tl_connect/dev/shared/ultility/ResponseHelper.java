package com.tl_connect.dev.shared.ultility;


import org.springframework.http.ResponseEntity;

import com.tl_connect.dev.shared.common.dto.ResponseWrapper;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;

public class ResponseHelper {

    public static <T> ResponseEntity<ResponseWrapper<T>> create(ResponseStatus responseStatus, String message, T data) {
        return ResponseEntity
        .status(responseStatus.getHttpStatus())
        .body(
            ResponseWrapper.<T>builder()
            .code(responseStatus.getCode())
            .message(message)
            .data(data)
            .build()
        );
    }
    
    public static <T> ResponseEntity<ResponseWrapper<T>> success(String message, T data) {
        return create(ResponseStatus.SUCCESS, message, data);
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> internalError(String message) {
        return create(ResponseStatus.INTERNAL_ERROR, message, null);
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> invalidInput(String message) {
        return create(ResponseStatus.INVALID_INPUT, message, null);
    }

}

