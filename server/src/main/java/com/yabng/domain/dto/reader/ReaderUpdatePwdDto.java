package com.yabng.domain.dto.reader;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReaderUpdatePwdDto {

    @NotEmpty(message = "旧密码不能为空")
    private String oldPwd;

    @NotEmpty(message = "新密码不能为空")
    private String newPwd;

    @NotEmpty(message = "确认密码不能为空")
    private String confirmPwd;
}
