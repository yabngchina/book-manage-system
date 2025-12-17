package com.yabng.domain.vo.borrow;

import com.yabng.domain.vo.reader.ReaderVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BorrowResponse {

    private ReaderVo reader;

    private List<BorrowVo> borrows = new ArrayList<>();
}
