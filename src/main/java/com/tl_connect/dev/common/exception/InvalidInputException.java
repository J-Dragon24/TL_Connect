package com.tl_connect.dev.common.exception;

import com.tl_connect.dev.common.enums.ResponseStatus;

public class InvalidInputException extends BaseException {
    public InvalidInputException(String message) {
        super(ResponseStatus.INVALID_INPUT, message);
    }
}
