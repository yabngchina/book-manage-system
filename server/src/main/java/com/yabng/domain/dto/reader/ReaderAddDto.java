package com.yabng.domain.dto.reader;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ReaderAddDto {
    /**
     * 读者姓名
     */
    @NotEmpty(message = "姓名不能为空")
    private String name;

    /**
     * 性别：男/女
     */
    @NotEmpty(message = "性别不能为空")
    private String sex;

    /**
     * 读者类别
     */
    @NotNull(message = "读者类别id不能为空")
    private Integer readerTypeId;

    /**
     * 单位代码/单位名称
     */
    @NotEmpty(message = "部门不能为空")
    private String department;

    /**
     * 读者照片
     */
    @NotNull(message = "用户头像不能为空")
    private MultipartFile photo;
}
