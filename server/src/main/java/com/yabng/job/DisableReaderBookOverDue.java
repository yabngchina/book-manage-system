package com.yabng.job;

import com.yabng.domain.pojo.Borrow;
import com.yabng.mapper.BorrowMapper;
import com.yabng.mapper.ReaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DisableReaderBookOverDue {

    @Autowired
    private ReaderMapper readerMapper;
    @Autowired
    private BorrowMapper borrowMapper;

    @Scheduled(cron = "*/15 * * * * *")
    @Transactional
    public void disableReader() {
        // 查询所有逾期未归还图书的借阅记录
        List<Borrow> overdue = borrowMapper.selectAllOverDueNotReturn();
        // 防止浪费性能
        if (overdue == null || overdue.isEmpty()) {
            return;
        }
        // 获取所有读者的id
        List<Integer> readerIds = overdue
                .stream()
                .map(Borrow::getReaderId)
                .toList();
        // 将这些读者的isAvailable字段修改为false
        readerMapper.updateIsAvailableByIds(false, readerIds);
    }
}
