import {defineStore} from "pinia";
import {ref} from "vue";


/**
 * 读者信息 Store
 */
export const useReaderInfoStore = defineStore('readerInfo',() => {
    // 用户信息
    const readerInfo = ref(null)

    const setReaderInfo = (value) => {
        readerInfo.value = value
    }

    const removeReaderInfo = () => {
        readerInfo.value = null
    }

    // 返回
    return {
        readerInfo,
        setReaderInfo,
        removeReaderInfo
    }
}, {
    persist: true
})