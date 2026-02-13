package com.tl_connect.dev.core.common.exception;

import com.tl_connect.dev.core.common.enums.ResponseStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(ResponseStatus.FORBIDDEN, message);
    }
}