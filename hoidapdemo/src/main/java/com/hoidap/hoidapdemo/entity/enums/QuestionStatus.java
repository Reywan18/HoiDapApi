package com.hoidap.hoidapdemo.entity.enums;

public enum QuestionStatus {
    PENDING(0, "Chờ trả lời"),
    ANSWER(1, "Đã trả lời");

    private final int code;
    private final String description;

    QuestionStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public static QuestionStatus fromCode(int code) {
        for (QuestionStatus status : QuestionStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}
