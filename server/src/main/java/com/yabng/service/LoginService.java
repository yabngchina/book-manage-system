package com.yabng.service;

import com.yabng.domain.dto.login.LoginRequest;
import com.yabng.domain.vo.login.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request, HttpServletResponse response);
}
