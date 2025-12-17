package com.yabng.service;

import com.yabng.domain.dto.borrow.BorrowBookDto;
import com.yabng.domain.dto.borrow.BorrowRenewDto;
import com.yabng.domain.dto.borrow.BorrowReturnDto;
import com.yabng.domain.vo.borrow.BorrowInfoVo;
import com.yabng.domain.vo.borrow.BorrowResponse;

public interface BorrowService {
    BorrowResponse queryNotReturnByReaderId(Integer readerId);

    Boolean borrowBook(BorrowBookDto borrowBookDto);

    BorrowInfoVo queryBorrowNotReturnByBookId(Integer bookId);

    Boolean renew(BorrowRenewDto borrowRenewDto);

    Boolean returnBook(BorrowReturnDto borrowReturnDto);
}
