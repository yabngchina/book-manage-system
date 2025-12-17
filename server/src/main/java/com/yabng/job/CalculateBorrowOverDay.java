package com.yabng.job;

import com.yabng.domain.pojo.Borrow;
import com.yabng.domain.pojo.Reader;
import com.yabng.domain.pojo.ReaderType;
import com.yabng.mapper.BorrowMapper;
import com.yabng.mapper.ReaderMapper;
import com.yabng.mapper.ReaderTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CalculateBorrowOverDay {

    @Autowired
    private BorrowMapper borrowMapper;
    @Autowired
    private ReaderMapper readerMapper;
    @Autowired
    private ReaderTypeMapper readerTypeMapper;


    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public void calculateOverDay() {
        log.info("开始处理逾期未还的书");
        // 查询所有逾期未归还的书
        List<Borrow> borrows = borrowMapper.getOverDueNotReturn();
        // 如果查询为空，直接结束
        if (borrows == null || borrows.isEmpty()) {
            log.info("没有逾期未归还的书");
            return;
        }
        log.info("查询到 {} 本书逾期未还", borrows.size());
        // 获取所有的读者id
        List<Integer> readerIds = borrows
                .stream()
                .map(Borrow::getReaderId)
                .distinct()
                .toList();
        // 查询读者
        List<Reader> readers = readerMapper.listByIds(readerIds);
        // 转换成map，key为readerId，value为readerTypeId
        Map<Integer, Integer> readerIdToReaderTypeId = readers
                .stream()
                .collect(Collectors.toMap(Reader::getId, Reader::getReaderTypeId));
        // 获取这些读者的类型id
        List<Integer> readerTypeIds = readers.stream()
                .map(Reader::getReaderTypeId)
                .distinct()
                .toList();
        // 查询这些读者类型
        List<ReaderType> readerTypes = readerTypeMapper.selectByIds(readerTypeIds);
        // 转换成map，key为readerTypeId，value为punishRate
        Map<Integer, BigDecimal> readerTypeIdToPunishRate = readerTypes
                .stream()
                .collect(Collectors.toMap(ReaderType::getId, ReaderType::getPunishRate));

        // 遍历
        for (Borrow borrow : borrows) {
            // 计算逾期天数
            // 当前系统时间
            LocalDateTime now = LocalDateTime.now();
            // 还书计划日期
            LocalDateTime dateRetPlan = borrow.getDateRetPlan();
            int overDays = calcOverDays(dateRetPlan, now);
            borrow.setOverDay(overDays);
            // 计算预期罚款金额
            // 获取当前读者类型id
            Integer readerTypeId = readerIdToReaderTypeId.get(borrow.getReaderId());
            // 获取罚款率
            BigDecimal punishRate = readerTypeIdToPunishRate.get(readerTypeId);
            // 计算超期罚款金额
            BigDecimal overMoney = punishRate.multiply(new BigDecimal(overDays));
            borrow.setOverMoney(overMoney);
        }
        // 批量更新
        borrowMapper.updateOverDayAndOverMoneyByIdInBatch(borrows);
        log.info("处理完成");
    }

    private int calcOverDays(LocalDateTime plan, LocalDateTime actual) {
        long days = ChronoUnit.DAYS.between(
                plan.toLocalDate(),
                actual.toLocalDate()
        );
        return Math.max(0, (int) days);
    }
}
