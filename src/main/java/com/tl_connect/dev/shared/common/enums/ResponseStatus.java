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
    DATABASE_ERROR(400, "Database error", -27),
    TOKEN_EXPIRED_OR_INVALID(401, "Invalid or expired QR token", -28),

    //enrollment
    ENROLLMENT_CONDITION_NOT_MET(400, "Enrollment condition not met", -100),
    SCHEDULE_CONFLICT(409, "Schedule conflict", -101),
    PRE_REQUISITE_NOT_MET(400, "Pre-requisite not met", -102),
    MAX_CREDIT_EXCEEDED(400, "Maximum credit exceeded", -103),
    DUPLICATE_SUBJECT(400, "Duplicate subject", -104),
    DUPLICATE_COURSE_CLASS(400, "Duplicate course class", -105),
    SUBJECT_ALREADY_PASSED(400, "Subject already passed", -106),
    CLASS_FULL(400, "Class full", -107),
    SUBJECT_NOT_IN_PROGRAM(400, "Subject not in program", -108),
    OUTSIDE_REGISTRATION_PERIOD(400, "Outside registration period", -109);

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
