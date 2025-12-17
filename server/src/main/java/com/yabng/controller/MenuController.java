package com.yabng.controller;

import com.yabng.domain.pojo.Menu;
import com.yabng.result.Result;
import com.yabng.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/get")
    public Result<List<Menu>> getCurrentUserMenu() {
        return Result.ok(menuService.getCurrentUserMenu());
    }
}
