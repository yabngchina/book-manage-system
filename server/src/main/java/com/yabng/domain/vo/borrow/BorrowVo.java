package com.yabng.domain.vo.borrow;

import com.yabng.domain.pojo.Book;
import com.yabng.domain.vo.book.BookVo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BorrowVo {

    /**
     * 借阅顺序号（自增，主键）
     */
    private Integer id;

    /**
     * 读者序号
     */
    private Integer readerId;

    /**
     * 图书序号
     */
    private Integer bookId;

    /**
     * 续借次数（第一次借时，记为0）
     */
    private Integer continueTimes;

    /**
     * 借书日期
     */
    private LocalDateTime dateOut;

    /**
     * 应还日期
     */
    private LocalDateTime dateRetPlan;

    /**
     * 实际还书日期
     */
    private LocalDateTime dateRetAct;

    /**
     * 超期天数，缺省为0
     */
    private Integer overDay;

    /**
     * 超期金额（应罚款金额），缺省为0
     */
    private BigDecimal overMoney;

    /**
     * 罚款金额，缺省为0
     */
    private BigDecimal punishMoney;

    /**
     * 是否已经还书，缺省为0-未还
     */
    private Boolean hasReturn;

    /**
     * 借书操作员
     */
    private String operatorBorrow;

    /**
     * 还书操作员
     */
    private String operatorReturn;

    private BookVo book;
}
