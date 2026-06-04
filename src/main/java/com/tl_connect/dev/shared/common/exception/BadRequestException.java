package com.tl_connect.dev.shared.common.exception;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(ResponseStatus.BAD_REQUEST, message);
    }
}
