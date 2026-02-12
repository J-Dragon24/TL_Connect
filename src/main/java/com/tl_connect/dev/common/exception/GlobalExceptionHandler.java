package com.tl_connect.dev.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tl_connect.dev.common.dto.ResponseWrapper;
import com.tl_connect.dev.common.enums.ResponseStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseWrapper<?>> handle(BaseException e) {

        ResponseStatus status = e.getStatus();

        return ResponseEntity
                .status(status.getHttpStatus())
                .body(ResponseWrapper.builder()
                        .code(status.getCode())
                        .message(e.getMessage())
                        .data(e.getData())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<?>> handle(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(e.getMessage())
                        .data(null)
                        .build());
    }

}
