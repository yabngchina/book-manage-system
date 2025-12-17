package com.yabng.domain.vo.login;

import com.yabng.domain.vo.reader.ReaderVo;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private ReaderVo reader;
}
