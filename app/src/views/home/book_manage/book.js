// 图书查询
export const BOOK_QUERY = {
    id: null,
    code: '',
    name: '',
    author: '',
    press: '',
    isbn: '',
    catalog: '',
    language: null,
    dateIn: '',
    status: '',
}

// 图书状态
export const STATUS_MAP = {
    AVAILABLE: '在馆',
    BORROWED: '借出',
    LOST: '遗失',
    SOLD: '变卖',
    DESTROYED: '销毁'
}

export const formatStatus = (status) => {
    return STATUS_MAP[status] ?? '未知'
}

// 图书语言
export const LANGUAGE_MAP = {
    0: '中文',
    1: '英文',
    2: '日文',
    3: '俄文',
    4: '德文',
    5: '法文'
}

export const formatLanguage = (language) => {
    return LANGUAGE_MAP[language] ?? '未知'
}