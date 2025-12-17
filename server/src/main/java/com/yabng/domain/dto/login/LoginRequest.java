package com.yabng.domain.dto.login;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {

    @NotNull(message = "账号不能为空")
    private String accountId;

    @NotEmpty(message = "密码不能为空")
    private String password;
}
