import axios from "axios";
import {useAuthStore} from "@/stores/auth";
import router from '@/router/index'
import {ElMessage} from "element-plus";

/**
 * axios 配置
 */
const axiosConfig = {
    baseURL: '/api',
    timeout: 5000,
    withCredentials: true
}
/**
 * 创建 axios 实例
 */
const instance = axios.create(axiosConfig)

/**
 * 添加请求拦截器
 */
instance.interceptors.request.use( config => {
    // 添加jwt token 到请求头
    const authStore = useAuthStore()

    if (authStore.token) {
        config.headers.Authorization = authStore.token
    }

    return config
},(err) => {
    return Promise.reject(err)
})

// 统一过滤空参数
instance.interceptors.request.use(config => {
    if (config.params) {
        config.params = Object.fromEntries(
            Object.entries(config.params)
                .filter(([_, v]) => v !== '' && v !== null && v !== undefined)
        )
    }
    return config
})

/**
 * 添加响应拦截器
 */
// 响应拦截器，处理响应数据
instance.interceptors.response.use((response) => {
    // 获取响应数据
    const result = response.data
    // 判断响应状态码
    if (result.code === 0) {
        // 友好提示
        ElMessage.error(result.message ? result.message : '服务异常')
        return Promise.reject(result)
    }
    // 否则返回响应数据
    return result
}, async (err) => {
    // 响应错误处理
    const {config, response: {status}} = err

    if (status !== 401) return Promise.reject(err) // 状态码不是401，则直接返回错误

    const authStore = useAuthStore()
    // 清除 jwt token
    authStore.clearToken()

    // 401 跳转登录界面
    await router.push('/login')
})

/**
 * 导出实例
 */
export default instance