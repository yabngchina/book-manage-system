import request from './request'

/**
 * 登录请求
 */
export const loginService = (data) => {
    return request.post('/login', data)
}