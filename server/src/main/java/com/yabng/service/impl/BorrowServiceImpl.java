package com.yabng.service.impl;

import com.yabng.domain.dto.borrow.BorrowBookDto;
import com.yabng.domain.dto.borrow.BorrowRenewDto;
import com.yabng.domain.dto.borrow.BorrowReturnDto;
import com.yabng.domain.pojo.Book;
import com.yabng.domain.pojo.Borrow;
import com.yabng.domain.pojo.Reader;
import com.yabng.domain.pojo.ReaderType;
import com.yabng.domain.vo.borrow.BorrowInfoVo;
import com.yabng.domain.vo.borrow.BorrowResponse;
import com.yabng.domain.vo.borrow.BorrowVo;
import com.yabng.domain.vo.reader.ReaderVo;
import com.yabng.enums.BookStatusEnum;
import com.yabng.enums.ReaderStatusEnum;
import com.yabng.exception.BaseException;
import com.yabng.mapper.BookMapper;
import com.yabng.mapper.BorrowMapper;
import com.yabng.mapper.ReaderMapper;
import com.yabng.mapper.ReaderTypeMapper;
import com.yabng.service.BookService;
import com.yabng.service.BorrowService;
import com.yabng.service.ReaderService;
import com.yabng.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowMapper borrowMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private ReaderMapper readerMapper;
    @Autowired
    private ReaderTypeMapper readerTypeMapper;
    @Lazy
    @Autowired
    private ReaderService readerService;
    @Lazy
    @Autowired
    private BookService bookService;

    @Override
    public BorrowResponse queryNotReturnByReaderId(Integer readerId) {
        // 查询用户未归还的图书
        List<Borrow> borrows = borrowMapper.getNotReturnByReaderId(readerId);
        // 查询用户信息
        ReaderVo reader = readerService.getVoById(readerId);

        BorrowResponse response = new BorrowResponse();
        response.setReader(reader);

        // 如果没有未归还的图书，直接返回
        if (borrows == null || borrows.isEmpty()) {
            return response;
        }

        // 有未归还的图书
        List<Integer> notReturnBookIds = borrows.stream().map(Borrow::getBookId).toList();
        List<Book> books = bookMapper.listByIds(notReturnBookIds);
        Map<Integer, Book> bookMap = books.stream().collect(Collectors.toMap(Book::getId, book -> book));
        // 封装响应结果
        List<BorrowVo> borrowVos = borrows
                .stream()
                .map(b -> {
                    BorrowVo borrowVo = new BorrowVo();
                    BeanUtils.copyProperties(b, borrowVo);
                    borrowVo.setBook(bookService.toBookVo(bookMap.get(b.getBookId())));
                    return borrowVo;
                })
                .toList();
        response.setBorrows(borrowVos);
        return response;
    }

    @Override
    @Transactional
    public Boolean borrowBook(BorrowBookDto borrowBookDto) {
        // 判断用户是否可以借书
        // 查询读者
        ReaderVo reader = readerService.getVoById(borrowBookDto.getReaderId());
        // 读者账户无法借书
        if (!reader.getIsAvailable()) {
            throw new BaseException("读者存在逾期未归还的书，无法借书");
        }
        // 1. 借书证状态为有效
        if (!ReaderStatusEnum.VALID.getStatus().equals(reader.getStatus())) {
            throw new BaseException("读者账号状态异常，无法借书");
        }

        // 图书是否存在
        Book book = bookMapper.getById(borrowBookDto.getBookId());
        if (book == null) {
            throw new BaseException("图书不存在");
        }
        // 判断图书状态是否为在馆
        if (!BookStatusEnum.AVAILABLE.getStatus().equals(book.getStatus())) {
            throw new BaseException("图书已" + book.getStatus());
        }

        // 2. 已借书数量小于可借书数量
        // 获取reader_type_id
        ReaderType readerType = readerTypeMapper.getById(reader.getReaderTypeId());
        if (reader.getBorrowQuantity() >= readerType.getCanLendQuantity()) {
            throw new BaseException("该读者已借书数量超出可借书数量");
        }

        // 3. 不存在超期未归还图书
        if (borrowMapper.existBorrowNotReturnAndOverDue(borrowBookDto.getReaderId())) {
            throw new BaseException("该读者有超期未归还图书");
        }

        // 查询操作员
        Reader operator = readerMapper.getById(UserContext.get());

        // 可以借书
        Borrow borrow = new Borrow();
        BeanUtils.copyProperties(borrowBookDto, borrow);
        borrow.setContinueTimes(0);
        borrow.setDateOut(LocalDateTime.now());
        borrow.setDateRetPlan(LocalDateTime.now().plusDays(readerType.getCanLendDay()));
        borrow.setDateRetAct(null);
        borrow.setOverDay(0);
        borrow.setOverMoney(BigDecimal.ZERO);
        borrow.setPunishMoney(BigDecimal.ZERO);
        borrow.setHasReturn(false);
        borrow.setOperatorBorrow(operator.getName());
        borrow.setOperatorReturn(null);
        // 新增
        borrowMapper.save(borrow);

        // 借书数量加1
        readerMapper.incrBorrowQuantityById(borrowBookDto.getReaderId());
        // 修改图书状态
        bookMapper.updateStatusById(BookStatusEnum.BORROWED.getStatus(), borrowBookDto.getBookId());

        return true;
    }

    @Override
    public BorrowInfoVo queryBorrowNotReturnByBookId(Integer bookId) {
        // 查询图书状态
        Book book = bookMapper.getById(bookId);
        if (book == null) {
            throw new BaseException("图书不存在");
        }
        // 查询借阅信息
        Borrow borrow = borrowMapper.getNotReturnByBookId(bookId);
        if (borrow == null) {
            throw new BaseException("图书已归还");
        }
        BorrowInfoVo borrowInfoVo = new BorrowInfoVo();
        borrowInfoVo.setBook(bookService.toBookVo(book));

        BorrowVo borrowVo = new BorrowVo();
        BeanUtils.copyProperties(borrow, borrowVo);
        borrowVo.setBook(bookService.toBookVo(book));
        borrowInfoVo.setBorrow(borrowVo);

        ReaderVo reader = readerService.getVoById(borrow.getReaderId());
        borrowInfoVo.setReader(reader);

        // 读者类型
        ReaderType readerType = readerTypeMapper.getById(reader.getReaderTypeId());
        borrowInfoVo.setReaderType(readerType);

        return borrowInfoVo;
    }

    @Override
    @Transactional
    public Boolean renew(BorrowRenewDto borrowRenewDto) {
        // 查询图书
        Book book = bookMapper.getById(borrowRenewDto.getBookId());
        if (book == null) {
            throw new BaseException("图书不存在");
        }
        // 判断图书状态是否异常
        if (!BookStatusEnum.BORROWED.getStatus().equals(book.getStatus())) {
            throw new BaseException("图书状态异常，无法续借");
        }

        // 查询读者
        ReaderVo reader = readerService.getVoById(borrowRenewDto.getReaderId());
        // 判断读者状态是否有效
        if (!ReaderStatusEnum.VALID.getStatus().equals(reader.getStatus())) {
            throw new BaseException("读者状态异常，无法续借");
        }
        // 获取读者类型
        ReaderType readerType = readerTypeMapper.getById(reader.getReaderTypeId());
        // 判断续借天数是否大于最大允许天数
        if (borrowRenewDto.getContinueDays() > readerType.getCanLendDay()) {
            throw new BaseException("续借天数超出最大允许天数");
        }

        // 获取借阅记录
        Borrow borrow = borrowMapper.getById(borrowRenewDto.getId());
        // 判断借阅记录是否存在
        if (borrow == null) {
            throw new BaseException("借阅记录不存在");
        }
        // 判断已续借次数是否大于等于该读者最大可续借次数
        if (borrow.getContinueTimes() >= readerType.getCanContinueTimes()) {
            throw new BaseException("已续借次数超出最大可续借次数");
        }

        // 续借
        borrow.setContinueTimes(borrow.getContinueTimes() + 1);
        borrow.setDateRetPlan(borrow.getDateRetPlan().plusDays(borrowRenewDto.getContinueDays()));
        borrowMapper.updateById(borrow);
        return true;
    }

    @Override
    @Transactional
    public Boolean returnBook(BorrowReturnDto borrowReturnDto) {
        // 查询借阅记录
        Borrow borrow = borrowMapper.getById(borrowReturnDto.getId());
        if (borrow == null) {
            throw new BaseException("借阅记录不存在");
        }
        // 查询图书
        Book book = bookMapper.getById(borrowReturnDto.getBookId());
        // 判断图书是否存在
        if (book == null) {
            throw new BaseException("图书不存在");
        }
        // 图书是否丢失
        if (BookStatusEnum.LOST.getStatus().equals(book.getStatus())) {
            // 图书丢失，修改罚款金额
            BigDecimal punishMoney = book.getPrice().multiply(BigDecimal.valueOf(3));
            // 修改借阅记录罚款金额
            borrowMapper.updatePunishMoneyById(punishMoney, borrowReturnDto.getId());
            throw new BaseException("图书已丢失");
        }
        // 图书状态异常
        if (!BookStatusEnum.BORROWED.getStatus().equals(book.getStatus())) {
            throw new BaseException("图书状态异常");
        }
        // 获取还书操作员信息
        Reader operatorReturn = readerMapper.getById(UserContext.get());
        // 实际还书日期
        borrow.setDateRetAct(LocalDateTime.now());
        // 已还书
        borrow.setHasReturn(true);
        // 还书操作员
        borrow.setOperatorReturn(operatorReturn.getName());
        borrowMapper.updateById(borrow);
        // 修改图书状态为在馆
        bookMapper.updateStatusById(BookStatusEnum.AVAILABLE.getStatus(), borrowReturnDto.getBookId());

        // 用户已归还全部书籍，isAvailable字段修改为true
        Reader reader = readerMapper.getById(borrowReturnDto.getReaderId());
        if (!reader.getIsAvailable() && !borrowMapper.existBookNotReturn(borrowReturnDto.getReaderId())) {
            // 用户isAvailable字段为false，不存在未归还的图书
            readerMapper.updateIsAvailableByIds(true, List.of(borrowReturnDto.getReaderId()));
        }
        return true;
    }
}
