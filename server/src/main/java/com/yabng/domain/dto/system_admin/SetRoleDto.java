package com.yabng.domain.dto.system_admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetRoleDto {

    @NotNull(message = "读者编号不能为空")
    private Integer readerId;

    @NotNull(message = "角色类型不能为空")
    private Integer roleId;
}
