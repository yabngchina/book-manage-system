import {defineStore} from "pinia";
import {ref} from "vue";

/**
 * 登录状态管理
 */
export const useAuthStore = defineStore('auth', () => {
    // jwt token
    const token = ref('')

    const setToken = (value) => {
        token.value = value
    }

    const clearToken = () => {
        token.value = ''
    }

    return {
        token,
        setToken,
        clearToken
    }
},{
    persist: true
})