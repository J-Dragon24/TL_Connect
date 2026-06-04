package com.tl_connect.dev.shared.common.exception;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class DuplicateRegistrationException extends BaseException {
    public DuplicateRegistrationException(ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
    }
}
