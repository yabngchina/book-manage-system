package com.yabng.enums;

import lombok.Getter;

@Getter
public enum AdminRoleEnum {
    READER(0, "读者"),
    LIBRARY_CARD_ADMIN(1, "借书证管理员"),
    BOOK_ADMIN(2, "图书管理员"),
    BORROW_ADMIN(3, "借阅管理员"),
    SYSTEM_ADMIN(4, "系统管理员"),
    ;

    private final Integer value;
    private final String role;

    AdminRoleEnum(Integer value, String role) {
        this.value = value;
        this.role = role;
    }

    public static AdminRoleEnum of(Integer value) {
        for (AdminRoleEnum roleEnum : values()) {
            if (roleEnum.value.equals(value)) {
                return roleEnum;
            }
        }
        return null;
    }
}
