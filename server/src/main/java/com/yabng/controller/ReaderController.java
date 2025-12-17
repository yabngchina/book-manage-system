package com.yabng.controller;

import com.github.pagehelper.PageInfo;
import com.yabng.anno.RoleControl;
import com.yabng.domain.dto.reader.ReaderAddDto;
import com.yabng.domain.dto.reader.ReaderTypeDto;
import com.yabng.domain.dto.reader.ReaderUpdateDto;
import com.yabng.domain.dto.reader.ReaderUpdatePwdDto;
import com.yabng.domain.pojo.ReaderType;
import com.yabng.domain.query.reader.ReaderQuery;
import com.yabng.domain.vo.reader.ReaderVo;
import com.yabng.enums.AdminRoleEnum;
import com.yabng.page.PageQuery;
import com.yabng.result.Result;
import com.yabng.service.ReaderService;
import com.yabng.utils.UserContext;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/reader")
public class ReaderController {

    @Autowired
    private ReaderService readerService;

    @PostMapping("/updatePwd")
    public Result<Boolean> updatePwd(@RequestBody @Validated ReaderUpdatePwdDto readerUpdatePwdDto) {
        return Result.ok(readerService.updatePwd(readerUpdatePwdDto));
    }

    @GetMapping("/query")
    public Result<List<ReaderVo>> query(ReaderQuery readerQuery) {
        return Result.ok(readerService.query(readerQuery));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.LIBRARY_CARD_ADMIN})
    @PostMapping("/add")
    public Result<ReaderVo> addReader(@Validated ReaderAddDto readerAddDto) {
        return Result.ok(readerService.save(readerAddDto));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.LIBRARY_CARD_ADMIN})
    @PostMapping("/add/batch")
    public Result<List<ReaderVo>> addReaderInBatch(
            @NotEmpty(message = "至少添加一个用户") List<ReaderAddDto> readerAddDtoList)
    {
        return Result.ok(readerService.addReaderInBatch(readerAddDtoList));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.LIBRARY_CARD_ADMIN})
    @PutMapping("/update")
    public Result<ReaderVo> updateReader(@Validated ReaderUpdateDto readerUpdateDto) {
        return Result.ok(readerService.update(readerUpdateDto));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.LIBRARY_CARD_ADMIN})
    @PostMapping("/reissue")
    public Result<ReaderVo> reissue(@RequestParam Integer id) {
        return Result.ok(readerService.reissue(id));
    }

    @PutMapping("/update/cancel")
    public Result<Boolean> cancelReader(@RequestParam Integer id) {
        return Result.ok(readerService.cancelReaderById(id));
    }

    @GetMapping("/query/{id}")
    public Result<ReaderVo> queryById(@PathVariable Integer id) {
        return Result.ok(readerService.getVoById(id));
    }

    /// ====================== reader type ======================

    @GetMapping("/type/query")
    public Result<List<ReaderType>> queryReaderType() {
        return Result.ok(readerService.queryReaderType());
    }

    @PostMapping("/type/add")
    public Result<ReaderType> addReaderType(@RequestBody @Validated ReaderTypeDto readerTypeDto) {
        return Result.ok(readerService.saveReaderType(readerTypeDto));
    }

    @PutMapping("/type/update")
    public Result<ReaderType> updateReaderType(
            @RequestBody @Validated(ReaderTypeDto.Update.class) ReaderTypeDto readerTypeDto
    ) {
        return Result.ok(readerService.updateReaderTypeById(readerTypeDto));
    }

    @DeleteMapping("/type/delete")
    public Result<Boolean> deleteReaderType(@RequestParam Integer id) {
        return Result.ok(readerService.deleteReaderTypeById(id));
    }

    @GetMapping("/type/query/page")
    public Result<PageInfo<ReaderType>> pageQueryType(PageQuery pageQuery) {
        return Result.ok(readerService.pageQueryType(pageQuery));
    }

    @GetMapping("/role/is-admin")
    public Result<Boolean> isAdmin() {
        return Result.ok(readerService.isAdmin(UserContext.get()));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.LIBRARY_CARD_ADMIN})
    @GetMapping("/query/page")
    public Result<PageInfo<ReaderVo>> pageQuery(ReaderQuery readerQuery) {
        return Result.ok(readerService.pageQuery(readerQuery));
    }
}
