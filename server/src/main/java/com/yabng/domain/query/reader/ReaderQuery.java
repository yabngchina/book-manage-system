package com.yabng.domain.query.reader;

import com.yabng.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReaderQuery extends PageQuery {

    private Integer id;

    private Integer readerTypeId;

    private String department;

    private String name;
}
