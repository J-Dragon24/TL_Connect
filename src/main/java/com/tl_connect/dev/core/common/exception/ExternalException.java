package com.tl_connect.dev.core.common.exception;

import com.tl_connect.dev.core.common.enums.ResponseStatus;

public class ExternalException extends BaseException {
    public ExternalException(String message) {
        super(ResponseStatus.EXTERNAL_ERROR, message);
    }
}
