package com.yabng.service.impl;

import com.yabng.domain.pojo.Menu;
import com.yabng.domain.vo.reader.ReaderVo;
import com.yabng.mapper.MenuMapper;
import com.yabng.service.MenuService;
import com.yabng.service.ReaderService;
import com.yabng.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private ReaderService readerService;

    @Override
    public List<Menu> getCurrentUserMenu() {
        // 获取当前用户角色
        ReaderVo reader = readerService.getVoById(UserContext.get());

        // 查询菜单
        List<Menu> menus = menuMapper.selectMenuByRoleId(reader.getAdminRoles());

        // 构建菜单树
        return buildMenuTree(menus);
    }

    /**
     * 构建菜单树
     */
    private List<Menu> buildMenuTree(List<Menu> menus) {
        Map<Long, Menu> menuMap = new HashMap<>();
        List<Menu> roots = new ArrayList<>();

        for (Menu menu : menus) {
            menuMap.put(menu.getId(), menu);
        }

        for (Menu menu : menus) {
            if (menu.getParentId() == 0) {
                roots.add(menu);
            } else {
                Menu parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        return roots;
    }
}
