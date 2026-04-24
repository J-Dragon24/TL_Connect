package com.tl_connect.dev.core.common.exception.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.tl_connect.dev.core.common.dto.ResponseWrapper;
import com.tl_connect.dev.core.common.enums.ResponseStatus;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Dữ liệu không hợp lệ");

        return ResponseEntity.badRequest().body(
                ResponseWrapper.builder()
                        .code(400)
                        .message(message)
                        .data(null)
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        Class<?> type = ex.getRequiredType();

        String message;

        if (type == LocalDate.class) {
            message = "Ngày không đúng định dạng (yyyy-MM-dd)";
        } else if (type == Long.class || type == Integer.class) {
            message = "Giá trị phải là số hợp lệ";
        } else if (type != null && type.isEnum()) {
            message = "Giá trị không hợp lệ";
        } else if (type == LocalDateTime.class) {
            message = "Thời gian không đúng định dạng (yyyy-MM-dd HH:mm:ss)";
        } else {
            message = "Dữ liệu không hợp lệ";
        }

        return ResponseEntity.badRequest().body(
            ResponseWrapper.builder()
                .code(400)
                .message(message)
                .data(null)
                .build()
        );
    }

}
