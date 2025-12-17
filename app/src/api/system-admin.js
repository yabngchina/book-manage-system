import request from "@/api/request";

/**
 * 查询所有的权限信息
 */
export const queryAllRolesService = () => {
    return request.get('/system/admin/query/all-type')
}

/**
 * 查询所有类型管理员，不包括系统管理员的权限信息
 */
export const queryAllAdminRolesService = () => {
    return request.get('/system/admin/query/admin-type')
}

/**
 * 更新非系统管理员权限
 */
export const updateRoleService = (data) => {
    return request.post('/system/admin/update/role', data)
}