package com.tl_connect.dev.common.exception;

import com.tl_connect.dev.common.enums.ResponseStatus;

public class UnauthorizeException extends BaseException {
    public UnauthorizeException(String message) {
        super(ResponseStatus.UNAUTHORIZED, message);
    }
}
