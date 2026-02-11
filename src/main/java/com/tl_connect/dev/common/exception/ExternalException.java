package com.tl_connect.dev.common.exception;

import com.tl_connect.dev.common.enums.ResponseStatus;

public class ExternalException extends BaseException {
    public ExternalException(String message) {
        super(ResponseStatus.EXTERNAL_ERROR, message);
    }
}
