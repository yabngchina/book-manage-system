package com.yabng.controller;

import com.github.pagehelper.PageInfo;
import com.yabng.anno.RoleControl;
import com.yabng.domain.dto.system_admin.SetRoleDto;
import com.yabng.domain.vo.reader.ReaderVo;
import com.yabng.domain.vo.system_admin.AdminRoleVo;
import com.yabng.enums.AdminRoleEnum;
import com.yabng.result.Result;
import com.yabng.service.SystemAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/admin")
public class SystemAdminController {

    @Autowired
    private SystemAdminService systemAdminService;

    @RoleControl(accessibleRoles = {
            AdminRoleEnum.SYSTEM_ADMIN,
            AdminRoleEnum.BOOK_ADMIN,
            AdminRoleEnum.BORROW_ADMIN,
            AdminRoleEnum.LIBRARY_CARD_ADMIN
    })
    @GetMapping("/query/all-type")
    public Result<List<AdminRoleVo>> queryAllRoles() {
        return Result.ok(systemAdminService.queryRoles());
    }

    @RoleControl(accessibleRoles = {
            AdminRoleEnum.SYSTEM_ADMIN,
            AdminRoleEnum.BOOK_ADMIN,
            AdminRoleEnum.BORROW_ADMIN,
            AdminRoleEnum.LIBRARY_CARD_ADMIN
    })
    @GetMapping("/query/admin-type")
    public Result<List<AdminRoleVo>> queryAdminRoles() {
        return Result.ok(systemAdminService.queryAdminRoles());
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN})
    @PostMapping("/update/role")
    public Result<Boolean> updateReaderRole(@RequestBody @Validated SetRoleDto setRoleDto) {
        return Result.ok(systemAdminService.updateReaderRoleByReaderId(setRoleDto));
    }
}
