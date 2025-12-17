package com.yabng.service;

import com.yabng.domain.pojo.Menu;

import java.util.List;

public interface MenuService {

    /**
     * 获取当前用户菜单
     */
    List<Menu> getCurrentUserMenu();
}
