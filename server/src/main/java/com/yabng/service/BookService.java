package com.yabng.service;

import com.github.pagehelper.PageInfo;
import com.yabng.domain.dto.book.BookDto;
import com.yabng.domain.pojo.Book;
import com.yabng.domain.query.book.BookQuery;
import com.yabng.domain.vo.book.BookVo;

import java.util.List;

public interface BookService {
    BookVo save(BookDto bookDto);

    Boolean deleteById(Integer id);

    Boolean updateById(BookDto bookDto);

    List<BookVo> list();

    Boolean updateStatusById(Integer id, String status);

    BookVo getById(Integer id);

    BookVo toBookVo(Book book);

    PageInfo<BookVo> pageQuery(BookQuery bookQuery);
}
