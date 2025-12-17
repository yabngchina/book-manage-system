package com.yabng.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yabng.domain.dto.book.BookDto;
import com.yabng.domain.pojo.Book;
import com.yabng.domain.query.book.BookQuery;
import com.yabng.domain.vo.book.BookVo;
import com.yabng.enums.BookStatusEnum;
import com.yabng.exception.BaseException;
import com.yabng.mapper.BookMapper;
import com.yabng.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    @Transactional
    public BookVo save(BookDto bookDto) {
        // 创建图书
        Book book = new Book();
        BeanUtils.copyProperties(bookDto, book);
        // 入馆时间
        book.setDateIn(LocalDateTime.now());
        // 封面
        try {
            book.setCover(bookDto.getCover().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 图书状态
        book.setStatus(BookStatusEnum.AVAILABLE.getStatus());
        // 新增
        bookMapper.save(book);
        return toBookVo(book);
    }

    @Override
    @Transactional
    public Boolean deleteById(Integer id) {
        bookMapper.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateById(BookDto bookDto) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDto, book);
        // 封面
        try {
            if (bookDto.getCover() != null) {
                book.setCover(bookDto.getCover().getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bookMapper.updateById(book);
        return true;
    }

    @Override
    public List<BookVo> list() {
        List<Book> books = bookMapper.listAll();
        if (books == null || books.isEmpty()) {
            return Collections.emptyList();
        }
        return books.stream()
                .map(this::toBookVo)
                .toList();
    }

    @Override
    @Transactional
    public Boolean updateStatusById(Integer id, String status) {
        bookMapper.updateStatusById(status, id);
        return true;
    }

    @Override
    public BookVo getById(Integer id) {
        Book book = bookMapper.getById(id);
        if (book == null) {
            throw new BaseException("图书不存在");
        }
        return toBookVo(book);
    }

    @Override
    public BookVo toBookVo(Book book) {
        BookVo bookVo = new BookVo();
        BeanUtils.copyProperties(book, bookVo);
        if (book.getCover() != null && book.getCover().length > 0) {
            bookVo.setCover("data:image/png;base64," + Base64.getEncoder().encodeToString(book.getCover()));
        }
        return bookVo;
    }

    @Override
    public PageInfo<BookVo> pageQuery(BookQuery bookQuery) {
        PageHelper.startPage(bookQuery.getPageNo(), bookQuery.getPageSize());
        // 查询
        List<Book> books = bookMapper.queryWithConditions(bookQuery);
        // page info
        PageInfo<Book> booksPageInfo = new PageInfo<>(books);
        // 将查询结果封装成vo
        List<BookVo> vos = booksPageInfo.getList()
                .stream()
                .map(this::toBookVo)
                .toList();
        PageInfo<BookVo> result = new PageInfo<>();
        BeanUtils.copyProperties(booksPageInfo, result);
        result.setList(vos);
        return result;
    }
}
