package com.tl_connect.dev.shared.common.exception;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class InvalidInputException extends BaseException {
    public InvalidInputException(String message) {
        super(ResponseStatus.INVALID_INPUT, message);
    }
}
