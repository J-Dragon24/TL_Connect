package com.tl_connect.dev.shared.common.exception;

import java.util.List;

import com.tl_connect.dev.shared.common.enums.ResponseStatus;
import com.tl_connect.dev.shared.common.exception.core.BaseException;

import lombok.Builder;
import lombok.Data;

public class EnrollmentConditionNotMetException extends BaseException {
    public EnrollmentConditionNotMetException(String message, Object violations) {
        super(ResponseStatus.ENROLLMENT_CONDITION_NOT_MET, message, violations);
    }

    @Data
    @Builder
    public static class MissingGroup {
        private Long groupId;
        private int needMore;
        private List<String> missingSubjectCodes;
    }
}
