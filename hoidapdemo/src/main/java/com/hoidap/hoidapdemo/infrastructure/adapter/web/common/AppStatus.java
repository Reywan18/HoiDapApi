package com.hoidap.hoidapdemo.infrastructure.adapter.web.common;

public enum AppStatus {
    SUCCESS(200, "Thanh cong"),

    USER_ALREADY_EXISTS(401, "Người dùng đã tồn tại"),
    INVALID_ROLE(402, "Vai trò không hợp lệ"),
    MISSING_VALUE(403, "Thiếu thông tin"),
    INVALID_REQUEST(404, "Dữ liệu đầu vào không hợp lệ"),

    INTERNAL_ERROR(500, "Lỗi hệ thống");

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
