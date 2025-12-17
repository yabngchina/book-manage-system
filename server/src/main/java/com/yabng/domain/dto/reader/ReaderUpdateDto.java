package com.yabng.domain.dto.reader;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class ReaderUpdateDto {
    /**
     * 读者编号/借书证号（主键）
     */
    @NotNull(message = "读者编号不能为空")
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
     * 读者照片
     */
    private MultipartFile photo;

    /**
     * 证件状态，3个：有效、挂失、注销
     */
    private String status;
}
