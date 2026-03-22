package com.tl_connect.dev.core.common.exception;

import com.tl_connect.dev.core.common.enums.ResponseStatus;
import com.tl_connect.dev.core.common.exception.core.BaseException;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(ResponseStatus.BAD_REQUEST, message);
    }
}
