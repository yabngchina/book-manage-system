package com.yabng.mapper;

import com.yabng.domain.pojo.Reader;
import com.yabng.domain.query.reader.ReaderQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReaderMapper {

    Reader getById(Integer id);

    void updatePwdById(String password, Integer id);

    List<Reader> query(ReaderQuery readerQuery);

    void save(Reader reader);

    void updateById(Reader reader);

    void saveBatch(List<Reader> readerList);

    void incrBorrowQuantityById(Integer id);

    List<Reader> listByIds(List<Integer> ids);

    void updateIsAvailableByIds(boolean isAvailable, List<Integer> ids);
}
