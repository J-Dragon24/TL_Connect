package com.tl_connect.dev.core.common.exception;

import com.tl_connect.dev.core.common.enums.ResponseStatus;
import com.tl_connect.dev.core.common.exception.core.BaseException;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(ResponseStatus.NOT_FOUND, message);
    }
}
