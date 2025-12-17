import {defineStore} from "pinia";
import {ref} from "vue";

/**
 * 菜单 Store
 */
export const useMenuStore = defineStore('menus',() => {
    // 用户信息
    const menus = ref([])

    const setMenus = (value) => {
        menus.value = value
    }

    const removeMenus = () => {
        menus.value = []
    }

    // 返回
    return {
        menus,
        setMenus,
        removeMenus
    }
}, {
    persist: true
})