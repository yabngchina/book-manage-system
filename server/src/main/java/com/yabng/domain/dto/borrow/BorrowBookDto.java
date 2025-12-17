package com.yabng.domain.dto.borrow;

import com.yabng.page.PageQuery;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class BorrowBookDto extends PageQuery {

    @NotNull(message = "读者编号不能为空")
    private Integer readerId;

    @NotNull(message = "图书编号不能为空")
    private Integer bookId;
}
