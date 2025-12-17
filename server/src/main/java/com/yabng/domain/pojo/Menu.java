package com.yabng.domain.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Menu {
    private Long id;
    private String name;
    private String path;
    private Long parentId;
    private Boolean requiresAdmin;
    private Integer orderNum;
    private Boolean visible;

    private List<Menu> children = new ArrayList<>();
}
