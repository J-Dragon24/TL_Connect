package com.tl_connect.dev.shared.common.exception;

import java.util.List;

import com.tl_connect.dev.shared.common.dto.FieldException;
import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

public class InvalidInputException extends BaseException {
    public InvalidInputException(String message) {
        super(ResponseStatus.INVALID_INPUT, message);
    }

    public InvalidInputException(String message, List<FieldException> errors) {
        super(ResponseStatus.INVALID_INPUT, message, errors);
    }
}
