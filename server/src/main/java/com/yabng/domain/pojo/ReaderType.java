package com.yabng.domain.pojo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 读者类别实体类
 */
@Data
public class ReaderType {
    /**
     * 读者类别（主键）
     */
    private Integer id;
    
    /**
     * 读者类别名称
     */
    private String name;
    
    /**
     * 可借书数量
     */
    private Integer canLendQuantity;
    
    /**
     * 可借书天数
     */
    private Integer canLendDay;
    
    /**
     * 可续借的次数
     */
    private Integer canContinueTimes;
    
    /**
     * 罚款率（元/天）
     */
    private BigDecimal punishRate;
    
    /**
     * 证书有效期（年）【非空，0表示永久有效】
     */
    private Integer dateValid;
}
