package com.yabng.service;

import com.yabng.domain.dto.system_admin.SetRoleDto;
import com.yabng.domain.vo.system_admin.AdminRoleVo;

import java.util.List;

public interface SystemAdminService {

    List<AdminRoleVo> queryRoles();

    List<AdminRoleVo> queryAdminRoles();

    Boolean updateReaderRoleByReaderId(SetRoleDto setRoleDto);
}
