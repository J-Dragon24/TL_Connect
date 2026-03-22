package com.tl_connect.dev.core.common.exception;

import com.tl_connect.dev.core.common.enums.ResponseStatus;
import com.tl_connect.dev.core.common.exception.core.BaseException;

public class InvalidInputException extends BaseException {
    public InvalidInputException(String message) {
        super(ResponseStatus.INVALID_INPUT, message);
    }
}
