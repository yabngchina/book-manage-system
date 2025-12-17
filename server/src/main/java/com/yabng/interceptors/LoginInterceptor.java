package com.yabng.interceptors;

import com.yabng.utils.JwtUtil;
import com.yabng.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        // 获取header中的token
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 判断token是否为空
        if (!StringUtils.hasLength(token) || JwtUtil.isTokenExpired(token)) {
            // 401 拒绝访问
            return reject(response, HttpStatus.UNAUTHORIZED);
        }

        // 解析token
        try {
            Claims claims = JwtUtil.parseToken(token);
            // 获取存储的用户id
            UserContext.set(Integer.parseInt(claims.get("id").toString()));
        } catch (Exception e) {
            // 解析失败，返回401
            // 401 拒绝访问
            return reject(response, HttpStatus.UNAUTHORIZED);
        }

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) throws Exception {
        // 清除存储的用户id，防止内存泄露
        UserContext.remove();
    }

    /**
     * 拒绝访问
     */
    private boolean reject(HttpServletResponse response, HttpStatus status) {
        response.setStatus(status.value());
        return false;
    }
}
