import request from './request'

/**
 * 更新密码
 */
export const updatePasswordService = (data) => {
    return request.post('/reader/updatePwd', data)
}

/**
 * 查询读者信息
 */
export const queryReaderService = (query) => {
    return request.get('/reader/query', {params: query})
}

/**
 * 新增读者
 */
export const addReaderService = (data) => {
    return request.post('/reader/add', data)
}

/**
 * 批量新增读者
 */
export const addReaderBatchService = (data) => {
    return request.post('/reader/add/batch', data)
}

/**
 * 更新读者信息
 */
export const updateReaderService = (data) => {
    return request.put('/reader/update', data)
}

/**
 * 重新发放借阅卡
 */
export const reissueReaderCardService = (query) => {
    return request.post('/reader/reissue', null, {params: query})
}

/**
 * 注销
 */
export const cancelReaderService = (query) => {
    return request.put('/reader/update/cancel', null, {params: query})
}

/// ====================== reader type ======================

/**
 * 查询所有读者类型
 */
export const queryAllReaderTypeService = () => {
    return request.get('/reader/type/query')
}

/**
 * 新增读者类型
 */
export const addReaderTypeService = (data) => {
    return request.post('/reader/type/add', data)
}

/**
 * 更新读者类型
 */
export const updateReaderTypeService = (data) => {
    return request.put('/reader/type/update', data)
}

/**
 * 删除读者类型
 */
export const deleteReaderTypeService = (query) => {
    return request.delete('/reader/type/delete', {params: query})
}

/**
 * 分页查询读者类型
 */
export const pageQueryReaderTypesService = (query) => {
    return request.get('/reader/type/query/page', {params: query})
}

export const isReaderAdminService = () => {
    return request.get('/reader/role/is-admin')
}

/**
 * 分页查询读者信息
 */
export const pageQueryReadersService = (query) => {
    return request.get('/reader/query/page', {params: query})
}