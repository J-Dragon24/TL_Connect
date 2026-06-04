package com.tl_connect.dev.shared.common.exception;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class ExternalException extends BaseException {
    public ExternalException(String message) {
        super(ResponseStatus.EXTERNAL_ERROR, message);
    }
}
