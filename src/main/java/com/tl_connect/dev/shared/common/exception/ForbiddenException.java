package com.tl_connect.dev.shared.common.exception;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(ResponseStatus.FORBIDDEN, message);
    }
}