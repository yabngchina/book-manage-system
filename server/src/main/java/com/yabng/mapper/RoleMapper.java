package com.yabng.mapper;

import com.yabng.domain.vo.system_admin.AdminRoleVo;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<AdminRoleVo> selectAll();

    List<AdminRoleVo> selectNonSuperAdminRoles();

    AdminRoleVo getById(Integer roleId);
}
