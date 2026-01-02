package com.yabng.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yabng.domain.dto.reader.ReaderAddDto;
import com.yabng.domain.dto.reader.ReaderTypeDto;
import com.yabng.domain.dto.reader.ReaderUpdateDto;
import com.yabng.domain.dto.reader.ReaderUpdatePwdDto;
import com.yabng.domain.pojo.Borrow;
import com.yabng.domain.pojo.Reader;
import com.yabng.domain.pojo.ReaderType;
import com.yabng.domain.query.reader.ReaderQuery;
import com.yabng.domain.vo.reader.ReaderVo;
import com.yabng.enums.AdminRoleEnum;
import com.yabng.enums.ReaderStatusEnum;
import com.yabng.exception.BaseException;
import com.yabng.mapper.BorrowMapper;
import com.yabng.mapper.ReaderMapper;
import com.yabng.mapper.ReaderTypeMapper;
import com.yabng.page.PageQuery;
import com.yabng.service.ReaderService;
import com.yabng.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class ReaderServiceImpl implements ReaderService {

    @Autowired
    private ReaderMapper readerMapper;

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private ReaderTypeMapper readerTypeMapper;

    @Override
    @Transactional
    public Boolean updatePwd(ReaderUpdatePwdDto readerUpdatePwdDto) {
        // 查询读者
        Reader reader = readerMapper.getById(UserContext.get());
        if (reader == null) {
            throw new BaseException("用户不存在");
        }
        // 校验旧密码
        if (!ObjectUtils.nullSafeEquals(readerUpdatePwdDto.getOldPwd(), reader.getPassword())) {
            throw new BaseException("旧密码错误");
        }
        // 校验新密码和确认密码
        if (!ObjectUtils.nullSafeEquals(readerUpdatePwdDto.getNewPwd(), readerUpdatePwdDto.getConfirmPwd())) {
            throw new BaseException("新密码和确认密码不一致");
        }
        // 更新
        readerMapper.updatePwdById(readerUpdatePwdDto.getNewPwd(), UserContext.get());
        return true;
    }

    @Override
    public List<ReaderVo> query(ReaderQuery readerQuery) {
        // 查询
        List<Reader> readers = readerMapper.query(readerQuery);
        if (readers == null || readers.isEmpty()) {
            return Collections.emptyList();
        }
        return readers.stream()
                .map(this::toReaderVo)
                .toList();
    }

    @Override
    @Transactional
    public ReaderVo save(ReaderAddDto readerAddDto) {
        // 准备新增
        Reader reader = new Reader();
        BeanUtils.copyProperties(readerAddDto, reader);
        try {
            reader.setPhoto(readerAddDto.getPhoto().getBytes());
            reader.setStatus(ReaderStatusEnum.VALID.getStatus());
            reader.setAdminRoles(AdminRoleEnum.READER.getValue());
        } catch (Exception e) {
            throw new RuntimeException("图片转换异常");
        }
        readerMapper.save(reader);
        // 封装vo返回
        return toReaderVo(reader);
    }

    @Override
    @Transactional
    public ReaderVo update(ReaderUpdateDto readerUpdateDto) {
        Reader reader = new Reader();
        BeanUtils.copyProperties(readerUpdateDto, reader);
        try {
            if (readerUpdateDto.getPhoto() != null) {
                reader.setPhoto(readerUpdateDto.getPhoto().getBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException("图片转换异常");
        }
        readerMapper.updateById(reader);
        return toReaderVo(reader);
    }

    @Override
    @Transactional
    public ReaderVo reissue(Integer id) {
        // 查询旧信息
        Reader oldReader = readerMapper.getById(id);
        if (oldReader == null) {
            throw new BaseException("用户不存在");
        }
        // 查询旧的借阅记录
        List<Borrow> oldBorrows = borrowMapper.selectByReaderId(id);

        // 复制到新的reader里面
        Reader newReader = new Reader();
        BeanUtils.copyProperties(oldReader, newReader);
        // id设置为null
        newReader.setId(null);
        // 设置状态为有效
        newReader.setStatus(ReaderStatusEnum.VALID.getStatus());
        // 新增
        readerMapper.save(newReader);
        // 按照新的信息，将旧的借阅记录转移到新的账户上
        List<Borrow> backupBorrows = oldBorrows
                .stream()
                .map(ob -> {
                    ob.setReaderId(newReader.getId());
                    return ob;
                })
                .toList();
        // 根据新生成的借阅卡id，保存用户旧的借阅记录
        borrowMapper.saveInBatch(backupBorrows);

        // 注销旧的
        oldReader.setStatus(ReaderStatusEnum.CANCELLED.getStatus());
        // 注销
        readerMapper.updateById(oldReader);

        ReaderVo readerVo = new ReaderVo();
        BeanUtils.copyProperties(newReader, readerVo);
        return readerVo;
    }

    @Override
    @Transactional
    public Boolean cancelReaderById(Integer id) {
        // 查询读者是否还有未归还的书
        boolean existBookNotReturn = borrowMapper.existBookNotReturn(id);
        // 如果有未归还书籍，抛出异常
        if (existBookNotReturn) {
            throw new BaseException("该读者有未归还的书");
        }
        // 注销
        // 查询用户
        Reader reader = readerMapper.getById(id);
        // 如果用户是系统管理员，无法注销该账号
        if (!ObjectUtils.nullSafeEquals(reader.getAdminRoles(), AdminRoleEnum.READER.getValue())) {
            throw new BaseException("管理员账号无法注销");
        }

        reader.setStatus(ReaderStatusEnum.CANCELLED.getStatus());
        readerMapper.updateById(reader);
        return true;
    }

    @Override
    public List<ReaderType> queryReaderType() {
        return readerTypeMapper.selectAll();
    }

    @Override
    @Transactional
    public ReaderType saveReaderType(ReaderTypeDto readerTypeDto) {
        ReaderType readerType = new ReaderType();
        BeanUtils.copyProperties(readerTypeDto, readerType);
        readerTypeMapper.save(readerType);
        return readerType;
    }

    @Override
    @Transactional
    public ReaderType updateReaderTypeById(ReaderTypeDto readerTypeDto) {
        ReaderType readerType = new ReaderType();
        BeanUtils.copyProperties(readerTypeDto, readerType);
        readerTypeMapper.update(readerType);
        return readerType;
    }

    @Override
    @Transactional
    public Boolean deleteReaderTypeById(Integer id) {
        readerTypeMapper.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public List<ReaderVo> addReaderInBatch(List<ReaderAddDto> readerAddDtoList) {
        List<Reader> readerList = new ArrayList<>(readerAddDtoList.size());

        for (ReaderAddDto readerAddDto : readerAddDtoList) {
            Reader reader = new Reader();
            BeanUtils.copyProperties(readerAddDto, reader);
            try {
                if (readerAddDto.getPhoto() != null) {
                    reader.setPhoto(readerAddDto.getPhoto().getBytes());
                }
            } catch (Exception e) {
                throw new RuntimeException("图片转换异常");
            }
            reader.setStatus(ReaderStatusEnum.VALID.getStatus());
            reader.setAdminRoles(AdminRoleEnum.READER.getValue());
            readerList.add(reader);
        }
        // 批量新增
        readerMapper.saveBatch(readerList);

        // 响应新增结果
        return readerList.stream()
                .map(this::toReaderVo)
                .toList();
    }

    @Override
    public Boolean isAdmin(Integer id) {
        Reader reader = readerMapper.getById(id);
        return !AdminRoleEnum.READER.getValue().equals(reader.getAdminRoles());
    }

    @Override
    public ReaderVo getVoById(Integer id) {
        Reader reader = readerMapper.getById(id);
        if (reader == null) {
            throw new BaseException("用户不存在");
        }
        return toReaderVo(reader);
    }

    @Override
    public ReaderVo toReaderVo(Reader reader) {
        ReaderVo readerVo = new ReaderVo();
        BeanUtils.copyProperties(reader, readerVo);
        // photo属性单独设置，编码成base64
        if (reader.getPhoto() != null && reader.getPhoto().length > 0) {
            readerVo.setPhoto("data:image/png;base64," + Base64.getEncoder().encodeToString(reader.getPhoto()));
        }
        return readerVo;
    }

    @Override
    public PageInfo<ReaderVo> pageQuery(ReaderQuery readerQuery) {
        PageHelper.startPage(readerQuery.getPageNo(), readerQuery.getPageSize());
        List<Reader> readers = readerMapper.query(readerQuery);
        PageInfo<Reader> pageInfo = new PageInfo<>(readers);
        List<ReaderVo> readerVos = pageInfo.getList().stream()
                .map(this::toReaderVo)
                .toList();
        PageInfo<ReaderVo> result = new PageInfo<>(readerVos);
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(readerVos);
        return result;
    }

    @Override
    public PageInfo<ReaderType> pageQueryType(PageQuery pageQuery) {
        PageHelper.startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        List<ReaderType> readerTypes = readerTypeMapper.selectAll();
        return new PageInfo<>(readerTypes);
    }
}
