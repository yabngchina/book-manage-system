package com.yabng.service.impl;

import com.yabng.domain.dto.login.LoginRequest;
import com.yabng.domain.pojo.Reader;
import com.yabng.domain.vo.login.LoginResponse;
import com.yabng.enums.ReaderStatusEnum;
import com.yabng.exception.BaseException;
import com.yabng.mapper.ReaderMapper;
import com.yabng.service.LoginService;
import com.yabng.service.ReaderService;
import com.yabng.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.yabng.constants.cookie.CookieConstants.ROLE;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private ReaderMapper readerMapper;
    @Lazy
    @Autowired
    private ReaderService readerService;

    @Override
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        // 查询用户
        Reader user = readerMapper.getById(Integer.parseInt(request.getAccountId()));
        // 判断用户是否存在
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        // 校验密码
        if (!ObjectUtils.nullSafeEquals(request.getPassword(), user.getPassword())) {
            throw new BaseException("密码错误");
        }
        // 判断用户是否可以正常登录
        if (ReaderStatusEnum.CANCELLED.getStatus().equals(user.getStatus())) {
            throw new BaseException("用户账户注销，无法登录");
        }
/*        if (!ReaderStatusEnum.isValid(user.getStatus())) {
            throw new BaseException("用户状态异常");
        }*/

        // 登录成功
        // 构造响应
        LoginResponse loginResponse = new LoginResponse();
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        // 过期时间
        Duration duration = Duration.ofDays(7);
        String token = JwtUtil.generateToken(claims, duration.toMillis());
        loginResponse.setToken(token);
        loginResponse.setReader(readerService.toReaderVo(user));

        // 将用户权限设置到cookie
        ResponseCookie cookie = ResponseCookie
                .from(ROLE, user.getAdminRoles().toString())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(duration)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return loginResponse;
    }
}
