package com.tl_connect.dev.core.common.exception;

import com.tl_connect.dev.core.common.enums.ResponseStatus;
import com.tl_connect.dev.core.common.exception.core.BaseException;

public class ConflictException extends BaseException{
    public ConflictException(String message) {
        super(ResponseStatus.CONFLICT, message);
    }
}
