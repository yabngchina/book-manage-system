package com.yabng.mapper;

import com.yabng.domain.pojo.Book;
import com.yabng.domain.query.book.BookQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {

    void save(Book book);

    void update(Book book);

    void updateStatusById(String status, Integer id);

    void deleteById(Integer id);

    void updateById(Book book);

    List<Book> listAll();

    List<Book> listByIds(List<Integer> ids);

    Book getById(Integer id);

    List<Book> queryWithConditions(BookQuery bookQuery);
}
