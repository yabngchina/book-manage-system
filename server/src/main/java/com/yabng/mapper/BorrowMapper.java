package com.yabng.mapper;

import com.yabng.domain.pojo.Borrow;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BorrowMapper {

    boolean existBookNotReturn(Integer readerId);

    List<Borrow> getNotReturnByReaderId(Integer readerId);

    boolean existBorrowNotReturnAndOverDue(Integer readerId);

    void save(Borrow borrow);

    Borrow getNotReturnByBookId(Integer bookId);

    Borrow getByReaderIdAndBookId(Integer readerId, Integer bookId);

    void incrContinueTimesById(Integer id);

    void updateById(Borrow borrow);

    boolean isBookNotReturn(Integer bookId);

    List<Borrow> getOverDueNotReturn();

    void updateOverDayAndOverMoneyByIdInBatch(List<Borrow> borrows);

    void updatePunishMoneyById(BigDecimal punishMoney, Integer id);

    Borrow getById(Integer id);

    List<Borrow> selectByReaderId(Integer readerId);

    void saveInBatch(List<Borrow> borrows);

    List<Borrow> selectAllOverDueNotReturn();
}
