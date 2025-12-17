package com.yabng.result;

import lombok.Data;

/**
 * 统一返回结果类
 */
@Data
public class Result<T> {

    private Integer code;

    private String message;

    private T data;

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(1, "ok", data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(1, "ok");
    }

    public static <T> Result<T> error() {
        return new Result<>(0, "error");
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(0, message);
    }
}
