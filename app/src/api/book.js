import request from '@/api/request'

/**
 * 新增图书
 */
export const addBookService = (data) => {
    return request.post('/book/add', data, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

/**
 * 删除图书
 */
export const deleteBookService = (query) => {
    return request.delete('/book/delete', {params: query})
}

/**
 * 更新图书
 */
export const updateBookService = (data) => {
    return request.put('/book/update', data)
}

/**
 * 查询所有图书
 */
export const queryAllBookService = () => {
    return request.get('/book/list')
}

/**
 * 更新图书状态
 */
export const updateBookStatusService = (data) => {
    return request.put('/book/update/status', data)
}

/**
 * 根据id查询图书
 */
export const queryBookByIdService = (query) => {
    return request.get('/book/queryById', {params: query})
}

/**
 * 分页查询图书
 */
export const pageQueryBooksService = (query) => {
    return request.get('/book/query/page', {params: query})
}