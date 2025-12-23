package com.hoidap.hoidapdemo.utils;

public enum AppStatus {
    SUCCESS(AppCode.SUCCESS, AppMessage.SUCCESS),

    USER_ALREADY_EXISTS(AppCode.UNAUTHORIZED, AppMessage.USER_ALREADY_EXISTS), // Ví dụ dùng 401
    INVALID_ROLE(AppCode.INVALID_REQUEST, AppMessage.INVALID_ROLE),
    MISSING_VALUE(AppCode.FORBIDDEN, AppMessage.MISSING_VALUE),
    INVALID_REQUEST_DATA(AppCode.INVALID_REQUEST, AppMessage.INVALID_REQUEST),

    INTERNAL_ERROR(AppCode.INTERNAL_SERVER_ERROR, AppMessage.INTERNAL_ERROR);
    private final int code;
    private final String message;

    AppStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
