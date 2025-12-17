package com.yabng.domain.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 读者信息实体类
 */
@Data
public class Reader {
    /**
     * 读者编号/借书证号（主键）
     */
    private Integer id;
    
    /**
     * 读者姓名
     */
    private String name;
    
    /**
     * 性别：男/女
     */
    private String sex;
    
    /**
     * 读者类别
     */
    private Integer readerTypeId;
    
    /**
     * 单位代码/单位名称
     */
    private String department;
    
    /**
     * 电话号码
     */
    private String phone;
    
    /**
     * 电子邮箱
     */
    private String email;
    
    /**
     * 读者登记日期/办证日期
     */
    private LocalDateTime dateRegister;
    
    /**
     * 读者照片
     */
    private byte[] photo;
    
    /**
     * 证件状态，3个：有效、挂失、注销
     */
    private String status;
    
    /**
     * 已借书数量
     */
    private Integer borrowQuantity;
    
    /**
     * 读者密码
     */
    private String password;
    
    /**
     * 管理角色：0-读者、1-借书证管理、2-图书管理、4-借阅管理、8-系统管理，可组合
     */
    private Integer adminRoles;

    /**
     * 账户是否存在未归还的书，账户是否可借书
     */
    private Boolean isAvailable;
}
