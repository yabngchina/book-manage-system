package com.yabng.controller;

import com.yabng.domain.dto.login.LoginRequest;
import com.yabng.domain.vo.login.LoginResponse;
import com.yabng.result.Result;
import com.yabng.service.LoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest loginRequest, HttpServletResponse response) {
        return Result.ok(loginService.login(loginRequest, response));
    }
}
