package com.tl_connect.dev.shared.common.exception;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class ErrorException extends BaseException {
    public ErrorException(ResponseStatus status, String message, Object data) {
        super(status, message, data);
    }

    public ErrorException(ResponseStatus status, String message) {
        super(status, message);
    }
}
