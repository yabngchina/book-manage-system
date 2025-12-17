package com.yabng.domain.vo.book;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 图书信息实体类
 */
@Data
public class BookVo {
    /**
     * 图书序号（主键，自增）
     */
    private Integer id;
    
    /**
     * 图书编号或条码号
     */
    private String code;
    
    /**
     * 书名
     */
    private String name;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 出版社
     */
    private String press;
    
    /**
     * 出版日期
     */
    private LocalDateTime datePress;
    
    /**
     * ISBN书号
     */
    private String isbn;
    
    /**
     * 分类号（如：TP316-21/123）
     */
    private String catalog;
    
    /**
     * 语言：0-中文，1-英文，2-日文，3-俄文，4-德文，5-法文
     */
    private Integer language;
    
    /**
     * 页数
     */
    private Integer pages;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 入馆日期
     */
    private LocalDateTime dateIn;
    
    /**
     * 内容简介
     */
    private String brief;
    
    /**
     * 图书封面照片
     */
    private String cover;
    
    /**
     * 图书状态：在馆、借出、遗失、变卖、销毁
     */
    private String status;
}
