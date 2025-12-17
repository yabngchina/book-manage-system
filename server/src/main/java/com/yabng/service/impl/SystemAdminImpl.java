package com.yabng.service.impl;

import com.yabng.domain.dto.system_admin.SetRoleDto;
import com.yabng.domain.pojo.Reader;
import com.yabng.domain.vo.system_admin.AdminRoleVo;
import com.yabng.enums.AdminRoleEnum;
import com.yabng.exception.BaseException;
import com.yabng.mapper.ReaderMapper;
import com.yabng.mapper.RoleMapper;
import com.yabng.service.SystemAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Service
public class SystemAdminImpl implements SystemAdminService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private ReaderMapper readerMapper;

    @Override
    public List<AdminRoleVo> queryRoles() {
        List<AdminRoleVo> roles = roleMapper.selectAll();
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles;
    }

    @Override
    public List<AdminRoleVo> queryAdminRoles() {
        List<AdminRoleVo> roles = roleMapper.selectNonSuperAdminRoles();
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles;
    }

    @Override
    @Transactional
    public Boolean updateReaderRoleByReaderId(SetRoleDto setRoleDto) {
        // 判断用户是否存在
        Reader reader = readerMapper.getById(setRoleDto.getReaderId());
        if (reader == null) {
            throw new BaseException("用户不存在");
        }
        // 判断权限是否存在
        AdminRoleVo role = roleMapper.getById(setRoleDto.getRoleId());
        if (role == null) {
            throw new BaseException("权限不存在");
        }
        // 判断权限是否相同，如果权限相同，则不需要修改
        if (ObjectUtils.nullSafeEquals(reader.getAdminRoles(), role.getId())) {
            return false;
        }
        // 无法修改系统管理员权限
        if (ObjectUtils.nullSafeEquals(reader.getAdminRoles(), AdminRoleEnum.SYSTEM_ADMIN.getValue())) {
            throw new BaseException("系统管理员权限无法修改");
        }
        // 修改权限
        reader.setAdminRoles(setRoleDto.getRoleId());
        readerMapper.updateById(reader);
        return true;
    }
}
