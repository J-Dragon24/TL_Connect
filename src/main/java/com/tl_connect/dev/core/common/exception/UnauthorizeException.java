package com.tl_connect.dev.core.common.exception;

import com.tl_connect.dev.core.common.enums.ResponseStatus;

public class UnauthorizeException extends BaseException {
    public UnauthorizeException(String message) {
        super(ResponseStatus.UNAUTHORIZED, message);
    }
}
