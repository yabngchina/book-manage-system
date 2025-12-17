package com.yabng.domain.vo.borrow;

import com.yabng.domain.pojo.Book;
import com.yabng.domain.pojo.ReaderType;
import com.yabng.domain.vo.book.BookVo;
import com.yabng.domain.vo.reader.ReaderVo;
import lombok.Data;

@Data
public class BorrowInfoVo {

    private BookVo book;

    private BorrowVo borrow;

    private ReaderVo reader;

    private ReaderType readerType;
}
