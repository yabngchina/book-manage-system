package com.yabng.page;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageQuery {

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNo = 1;

    @NotNull(message = "页大小不能为空")
    @Min(value = 1, message = "页大小不能小于1")
    private Integer pageSize = 10;
}
