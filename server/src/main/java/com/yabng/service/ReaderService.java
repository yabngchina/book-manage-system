package com.yabng.service;

import com.github.pagehelper.PageInfo;
import com.yabng.domain.dto.reader.ReaderAddDto;
import com.yabng.domain.dto.reader.ReaderTypeDto;
import com.yabng.domain.dto.reader.ReaderUpdateDto;
import com.yabng.domain.dto.reader.ReaderUpdatePwdDto;
import com.yabng.domain.pojo.Reader;
import com.yabng.domain.pojo.ReaderType;
import com.yabng.domain.query.reader.ReaderQuery;
import com.yabng.domain.vo.reader.ReaderVo;
import com.yabng.page.PageQuery;

import java.util.List;

public interface ReaderService {
    Boolean updatePwd(ReaderUpdatePwdDto readerUpdatePwdDto);

    List<ReaderVo> query(ReaderQuery readerQuery);

    ReaderVo save(ReaderAddDto readerAddDto);

    ReaderVo update(ReaderUpdateDto readerUpdateDto);

    ReaderVo reissue(Integer id);

    Boolean cancelReaderById(Integer id);

    List<ReaderType> queryReaderType();

    ReaderType saveReaderType(ReaderTypeDto readerTypeDto);

    ReaderType updateReaderTypeById(ReaderTypeDto readerTypeDto);

    Boolean deleteReaderTypeById(Integer id);

    List<ReaderVo> addReaderInBatch(List<ReaderAddDto> readerAddDtoList);

    Boolean isAdmin(Integer id);

    ReaderVo getVoById(Integer id);

    ReaderVo toReaderVo(Reader reader);

    PageInfo<ReaderVo> pageQuery(ReaderQuery readerQuery);

    PageInfo<ReaderType> pageQueryType(PageQuery pageQuery);
}
