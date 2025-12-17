package com.yabng.enums;

import lombok.Getter;

@Getter
public enum ReaderStatusEnum {
    LOSS("挂失"),
    CANCELLED("注销"),
    VALID("有效"),
    ;
    private final String status;

    ReaderStatusEnum(String status) {
        this.status = status;
    }

    public static boolean isValid(String status) {
        return VALID.status.equals(status);
    }
}
