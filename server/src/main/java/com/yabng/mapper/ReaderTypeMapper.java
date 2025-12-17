package com.yabng.mapper;

import com.yabng.domain.pojo.ReaderType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReaderTypeMapper {
    List<ReaderType> selectAll();

    void save(ReaderType readerType);

    void update(ReaderType readerType);

    void deleteById(Integer id);

    ReaderType getById(Integer id);

    List<ReaderType> selectByIds(List<Integer> ids);
}
