package com.yabng.aop;

import com.yabng.anno.RoleControl;
import com.yabng.enums.AdminRoleEnum;
import com.yabng.exception.BaseException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.yabng.constants.cookie.CookieConstants.ROLE;

@Aspect
@Component
public class AdminRoleControlAspect {

    @Pointcut("@annotation(com.yabng.anno.RoleControl) && execution(* com.yabng.controller.*.*(..))")
    public void AdminRoleControlPointCut() {}

    @Around("AdminRoleControlPointCut()")
    public Object AdminRoleControlAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取http请求上下文
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        // 判断是否为null
        if (requestAttributes == null) {
            return joinPoint.proceed();
        }
        // 获取当前线程绑定的Http请求
        HttpServletRequest request = requestAttributes.getRequest();

        // 获取cookies
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return joinPoint.proceed();
        }
        // 通过stream流获取cookie中存储的role value
        String roleVal = Arrays.stream(cookies)
                .filter(c -> ROLE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        // 判断cookie是否为null
        if (roleVal == null) {
            throw new BaseException("缺少必要的cookie");
        }

        // 获取当前执行方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上的注解
        RoleControl anno = method.getAnnotation(RoleControl.class);

        List<AdminRoleEnum> accessibleRoles = new ArrayList<>(Arrays.asList(anno.accessibleRoles()));
        // 判断当前用户的roleVal是否在accessibleRoles中
        for (AdminRoleEnum accessibleRole : accessibleRoles) {
            if (roleVal.equals(accessibleRole.getValue().toString())) {
                // 如果当前用户身份权限足够，则继续执行
                return joinPoint.proceed();
            }
        }

        // 遍历不到对应权限，抛出异常
        throw new BaseException("权限不足");
    }
}
