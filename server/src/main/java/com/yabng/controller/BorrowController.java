package com.yabng.controller;

import com.yabng.anno.RoleControl;
import com.yabng.domain.dto.borrow.BorrowBookDto;
import com.yabng.domain.dto.borrow.BorrowRenewDto;
import com.yabng.domain.dto.borrow.BorrowReturnDto;
import com.yabng.domain.vo.borrow.BorrowInfoVo;
import com.yabng.domain.vo.borrow.BorrowResponse;
import com.yabng.enums.AdminRoleEnum;
import com.yabng.result.Result;
import com.yabng.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BORROW_ADMIN})
    @GetMapping("/query/reader/not-return")
    public Result<BorrowResponse> queryNotReturn(@RequestParam Integer readerId) {
        return Result.ok(borrowService.queryNotReturnByReaderId(readerId));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BORROW_ADMIN})
    @PostMapping("/book")
    public Result<Boolean> borrowBook(@RequestBody BorrowBookDto borrowBookDto) {
        return Result.ok(borrowService.borrowBook(borrowBookDto));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BORROW_ADMIN})
    @GetMapping("/queryByBookId")
    public Result<BorrowInfoVo> queryByBookId(@RequestParam Integer bookId) {
        return Result.ok(borrowService.queryBorrowNotReturnByBookId(bookId));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BORROW_ADMIN})
    @PutMapping("/renew")
    public Result<Boolean> renew(@RequestBody @Validated BorrowRenewDto borrowRenewDto) {
        return Result.ok(borrowService.renew(borrowRenewDto));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BORROW_ADMIN})
    @PostMapping("/return")
    public Result<Boolean> returnBook(@RequestBody @Validated BorrowReturnDto borrowReturnDto) {
        return Result.ok(borrowService.returnBook(borrowReturnDto));
    }
}
