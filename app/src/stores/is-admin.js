import {defineStore} from "pinia";
import {ref} from "vue";
import {isReaderAdminService} from "@/api/reader";


/**
 * is admin Store
 */
export const useIsAdminStore = defineStore('isAdmin',() => {
    // 用户信息
    const isAdmin = ref(null)

    const setIsAdmin = (value) => {
        isAdmin.value = value
    }

    const removeIsAdmin = () => {
        isAdmin.value = null
    }

    // 返回
    return {
        isAdmin,
        setIsAdmin,
        removeIsAdmin
    }
}, {
    persist: true
})