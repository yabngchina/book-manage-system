package com.yabng.domain.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookDto {
    /**
     * 图书序号（主键，自增）
     */
    @NotNull(message = "id不能为空", groups = {Update.class})
    private Integer id;

    /**
     * 图书编号或条码号
     */
    @NotEmpty(message = "编号不能为空")
    private String code;

    /**
     * 书名
     */
    @NotEmpty(message = "书名不能为空")
    private String name;

    /**
     * 作者
     */
    @NotEmpty(message = "作者不能为空")
    private String author;

    /**
     * 出版社
     */
    @NotEmpty(message = "出版社不能为空")
    private String press;

    /**
     * 出版日期
     */
    @NotNull(message = "出版日期不能为空")
    private LocalDateTime datePress;

    /**
     * ISBN书号
     */
    @NotEmpty(message = "ISBN不能为空")
    private String isbn;

    /**
     * 分类号（如：TP316-21/123）
     */
    @NotEmpty(message = "分类号不能为空")
    private String catalog;

    /**
     * 语言：0-中文，1-英文，2-日文，3-俄文，4-德文，5-法文
     */
    @NotNull(message = "语言不能为空")
    @Size(max = 5, message = "语言范围错误")
    private Integer language;

    /**
     * 页数
     */
    @NotNull(message = "页数不能为空")
    @Min(value = 1, message = "页数不能小于1")
    private Integer pages;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能小于0")
    private BigDecimal price;

    /**
     * 内容简介
     */
    @NotEmpty(message = "内容简介不能为空")
    private String brief;

    /**
     * 图书封面照片
     */
    @NotNull(message = "封面不能为空", groups = {Add.class})
    private MultipartFile cover;

    /**
     * 图书状态：在馆、借出、遗失、变卖、销毁
     */
    @NotEmpty(message = "状态不能为空", groups = {Update.class})
    private String status;

    public interface Add {}

    public interface Update {}
}
