package com.tl_connect.dev.shared.common.exception;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class UnauthorizeException extends BaseException {
    public UnauthorizeException(String message) {
        super(ResponseStatus.UNAUTHORIZED, message);
    }
}
