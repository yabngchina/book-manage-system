package com.yabng.mapper;

import com.yabng.domain.pojo.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {

    List<Menu> selectMenuByRoleId(Integer roleId);
}
