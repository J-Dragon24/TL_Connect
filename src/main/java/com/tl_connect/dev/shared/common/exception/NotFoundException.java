package com.tl_connect.dev.shared.common.exception;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(ResponseStatus.NOT_FOUND, message);
    }
}
