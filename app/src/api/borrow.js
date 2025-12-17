import request from "@/api/request";

/**
 * 查询读者未归还的图书
 */
export const queryReaderNotReturnBookService = (query) => {
    return request.get('/borrow/query/reader/not-return', {params: query})
}

/**
 * 借书
 */
export const borrowBookService = (data) => {
    return request.post('/borrow/book', data)
}

/**
 * 查询图书的借阅记录
 */
export const queryBookBorrowRecordService = (query) => {
    return request.get('/borrow/queryByBookId', {params: query})
}

/**
 * 续借
 */
export const renewBookService = (data) => {
    return request.put('/borrow/renew', data)
}

/**
 * 还书
 */
export const returnBookService = (data) => {
    return request.post('/borrow/return', data)
}