package com.tl_connect.dev.shared.common.enums;

public enum ResponseStatus {
    SUCCESS(200, "Success", 0),
    INVALID_INPUT(400, "Invalid input provided", -1),
    NOT_FOUND(404, "Resource not found", -2),
    UNAUTHORIZED(401, "Authentication required", -3),
    FORBIDDEN(403, "Access denied", -4),
    VALIDATION_ERROR(400, "Validation error", -5),
    INTERNAL_ERROR(500, "Internal server error", -10),
    EXTERNAL_ERROR(502, "External service error", -13),
    CONFLICT(409, "Resource already exists", -25),
    BAD_REQUEST(400, "Bad request", -26),
    ENROLLMENT_CONDITION_NOT_MET(400, "Enrollment condition not met", -27);

    private final int httpStatus;
    private final String message;
    private final int code;

    ResponseStatus(int httpStatus, String message, int code) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
