package com.yabng.domain.vo.reader;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReaderVo {
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
     * 读者登记日期/办证日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime dateRegister;

    /**
     * 读者照片
     */
    private String photo;

    /**
     * 证件状态，3个：有效、挂失、注销
     */
    private String status;

    /**
     * 已借书数量
     */
    private Integer borrowQuantity;

    /**
     * 管理角色：0-读者、1-借书证管理、2-图书管理、4-借阅管理、8-系统管理，可组合
     */
    private Integer adminRoles;

    /**
     * 账户是否存在未归还的书，账户是否可借书
     */
    private Boolean isAvailable;
}
