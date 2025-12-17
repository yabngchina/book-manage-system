package com.yabng.handler;

import com.yabng.exception.BaseException;
import com.yabng.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public Result<Object> handleException(Exception e, HttpServletRequest request) {
        // 记录日志
        log.error("全局异常处理：{}", e.getMessage(), e);
        return Result.error("服务异常");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = BaseException.class)
    public Result<Object> handleBaseException(BaseException e, HttpServletRequest request) {
        // 记录日志
        log.error("全局异常处理：{}", e.getMessage(), e);
        return Result.error(e.getMessage());
    }
}
