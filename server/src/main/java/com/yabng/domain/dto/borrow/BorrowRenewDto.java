package com.yabng.domain.dto.borrow;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRenewDto {

    @NotNull(message = "借阅编号不能为空")
    private Integer id;

    @NotNull(message = "读者编号不能为空")
    private Integer readerId;

    @NotNull(message = "图书编号不能为空")
    private Integer bookId;

//    @NotNull(message = "续借天数不能为空")
//    @Min(value = 1, message = "续借天数不能小于1")
//    private Integer continueDays;
}
