package com.yabng.controller;

import com.github.pagehelper.PageInfo;
import com.yabng.anno.RoleControl;
import com.yabng.domain.dto.book.BookDto;
import com.yabng.domain.query.book.BookQuery;
import com.yabng.domain.vo.book.BookVo;
import com.yabng.enums.AdminRoleEnum;
import com.yabng.result.Result;
import com.yabng.service.BookService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BOOK_ADMIN})
    @PostMapping("/add")
    public Result<BookVo> addBook(@Validated(BookDto.Add.class) BookDto bookDto) {
        return Result.ok(bookService.save(bookDto));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BOOK_ADMIN})
    @DeleteMapping("/delete")
    public Result<Boolean> deleteBook(@RequestParam Integer id) {
        return Result.ok(bookService.deleteById(id));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BOOK_ADMIN})
    @PutMapping("/update")
    public Result<Boolean> updateBook(@Validated(BookDto.Update.class) BookDto bookDto) {
        return Result.ok(bookService.updateById(bookDto));
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BOOK_ADMIN})
    @GetMapping("/list")
    public Result<List<BookVo>> listBooks() {
        return Result.ok(bookService.list());
    }

    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BOOK_ADMIN})
    @PutMapping("/update/status")
    public Result<Boolean> updateBookStatus(@RequestParam @NotNull(message = "id不能为空") Integer id,
                                            @RequestParam @NotEmpty(message = "状态不能为空") String status) {
        return Result.ok(bookService.updateStatusById(id, status));
    }

    @RoleControl(accessibleRoles = {
            AdminRoleEnum.SYSTEM_ADMIN,
            AdminRoleEnum.BOOK_ADMIN,
            AdminRoleEnum.BORROW_ADMIN
    })
    @GetMapping("/queryById")
    public Result<BookVo> queryById(@RequestParam Integer id) {
        return Result.ok(bookService.getById(id));
    }

    // 分页查询
    @RoleControl(accessibleRoles = {AdminRoleEnum.SYSTEM_ADMIN, AdminRoleEnum.BOOK_ADMIN})
    @GetMapping("/query/page")
    public Result<PageInfo<BookVo>> pageQuery(BookQuery bookQuery) {
        return Result.ok(bookService.pageQuery(bookQuery));
    }

}
