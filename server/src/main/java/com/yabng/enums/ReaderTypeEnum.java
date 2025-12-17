package com.yabng.enums;

import lombok.Getter;

@Getter
public enum ReaderTypeEnum {
    TEACHER(10, "教师"),
    UNDERGRADUATE(20, "本科生"),
    COLLEGE_STUDENT(21, "专科生"),
    MASTER_DEGREE_STUDENT(30, "硕士研究生"),
    PHD_STUDENT(31, "博士研究生"),
    ;

    private final Integer value;

    private final String desc;

    ReaderTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
