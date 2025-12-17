import request from "@/api/request";

/**
 * 获取菜单
 */
export const getMenuService = () => {
    return request.get('/menu/get')
}