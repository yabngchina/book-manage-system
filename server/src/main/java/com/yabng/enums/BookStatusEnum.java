package com.yabng.enums;

import lombok.Getter;

@Getter
public enum BookStatusEnum {
    AVAILABLE("在馆"),
    BORROWED("借出"),
    LOST("遗失"),
    SOLD("变卖"),
    DESTROYED("销毁")
    ;

    private final String status;

    BookStatusEnum(String status) {
        this.status = status;
    }
}
