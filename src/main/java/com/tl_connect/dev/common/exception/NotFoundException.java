package com.tl_connect.dev.common.exception;

import com.tl_connect.dev.common.enums.ResponseStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(ResponseStatus.NOT_FOUND, message);
    }
}
