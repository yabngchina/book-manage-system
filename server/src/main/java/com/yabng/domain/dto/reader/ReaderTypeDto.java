package com.yabng.domain.dto.reader;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 读者类别实体类
 */
@Data
public class ReaderTypeDto {
    /**
     * 读者类别（主键）
     */
    @NotNull(message = "id不能为空", groups = {Update.class})
    private Integer id;

    /**
     * 读者类别名称
     */
    @NotEmpty(message = "名称不能为空")
    private String name;
    
    /**
     * 可借书数量
     */
    @NotNull(message = "可借数量不能为空")
    private Integer canLendQuantity;
    
    /**
     * 可借书天数
     */
    @NotNull(message = "可借天数不能为空")
    private Integer canLendDay;
    
    /**
     * 可续借的次数
     */
    @NotNull(message = "可续借次数不能为空")
    private Integer canContinueTimes;
    
    /**
     * 罚款率（元/天）
     */
    @NotNull(message = "罚款率不能为空")
    private BigDecimal punishRate;
    
    /**
     * 证书有效期（年）【非空，0表示永久有效】
     */
    @NotNull(message = "证书有效期不能为空")
    private Integer dateValid;

    public interface Update {}
}
