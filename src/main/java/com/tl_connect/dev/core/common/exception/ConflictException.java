package com.tl_connect.dev.core.common.exception;

import com.tl_connect.dev.core.common.enums.ResponseStatus;

public class ConflictException extends BaseException{
    public ConflictException(String message) {
        super(ResponseStatus.CONFLICT, message);
    }
}
