package com.tl_connect.dev.common.exception;

import com.tl_connect.dev.common.enums.ResponseStatus;

public abstract class BaseException extends RuntimeException {
    private final Object data;
    private final ResponseStatus status;

    protected BaseException(ResponseStatus status, String message, Object data) {
        super(message);
        this.data = data;
        this.status = status;
    }

    protected BaseException(ResponseStatus status, String message) {
        super(message);
        this.data = null;
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public int getCode() {
        return status.getCode();
    }

    public String getDefaultMessage() {
        return status.getMessage();
    }

    public int getHttpStatus() {
        return status.getHttpStatus();
    }
}
