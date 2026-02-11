package com.tl_connect.dev.common.enums;

public enum ResponseStatus {
    SUCCESS(200, "Success", 0),
    INVALID_INPUT(400, "Invalid input provided", -1),
    NOT_FOUND(404, "Resource not found", -2),
    UNAUTHORIZED(401, "Authentication required", -3),
    FORBIDDEN(403, "Access denied", -4),
    INTERNAL_ERROR(500, "Internal server error", -10),
    EXTERNAL_ERROR(502, "External service error", -13);

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
