import {createRouter, createWebHistory} from "vue-router";
import {useAuthStore} from "@/stores/auth";

/**
 * 定义路由关系
 */
const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/login/Login.vue')
    },
    {
        path: '/home',
        name: 'home',
        redirect: '/home/book',
        component: () => import('@/views/home/Home.vue'),
        children: [
            {
                path: 'book',
                name: 'book',
                redirect: '/home/book/add',
                children: [
                    {
                        path: 'add',
                        name: 'book-add',
                        meta: { requiresAdmin: true },
                        component: () => import('@/views/home/book_manage/BookTransact.vue')
                    },
                    {
                        path: 'maintain',
                        name: 'book-maintain',
                        meta: { requiresAdmin: true },
                        component: () => import('@/views/home/book_manage/BookMaintain.vue')
                    }
                ]
            },
            {
                path: 'reader',
                name: 'reader',
                redirect: '/home/reader/library-card/transact',
                children: [
                    {
                        path: 'library-card/transact',
                        name: 'reader-library-card-transact',
                        component: () => import('@/views/home/reader_manage/CardTransact.vue')
                    },
                    {
                        path: 'library-card/update',
                        name: 'reader-library-card-update',
                        component: () => import('@/views/home/reader_manage/CardUpdate.vue')
                    },
                    {
                        path: 'library-card/loss',
                        name: 'reader-library-card-loss',
                        component: () => import('@/views/home/reader_manage/CardLost.vue')
                    },
                    {
                        path: 'library-card/cancel',
                        name: 'reader-library-card-cancel',
                        component: () => import('@/views/home/reader_manage/CardCancel.vue')
                    },
                    {
                        path: 'type',
                        name: 'reader-type',
                        meta: { requiresAdmin: true },
                        component: () => import('@/views/home/reader_manage/ReaderTypeManage.vue')
                    }
                ]
            },
            {
                path: 'borrow',
                name: 'borrow',
                redirect: '/home/borrow/book',
                children: [
                    {
                        path: 'book',
                        name: 'borrow-book',
                        component: () => import('@/views/home/borrow_manage/BorrowBook.vue')
                    },
                    {
                        path: 'renew',
                        name: 'borrow-renew',
                        component: () => import('@/views/home/borrow_manage/RenewBook.vue')
                    },
                    {
                        path: 'return',
                        name: 'borrow-return',
                        component: () => import('@/views/home/borrow_manage/ReturnBook.vue')
                    }
                ]
            },
            {
                path: 'user',
                name: 'user',
                redirect: '/home/user/permission',
                children: [
                    {
                        path: 'permission',
                        name: 'user-permission',
                        meta: { requiresAdmin: true },
                        component: () => import('@/views/home/user_manage/Permission.vue')
                    },
                    {
                        path: 'update-password',
                        name: 'user-update-password',
                        component: () => import('@/views/home/user_manage/PasswordUpdate.vue')
                    }
                ]
            }
        ]
    }
]

/**
 * 创建路由
 */
const router = createRouter({
    history: createWebHistory(),
    routes
})

/**
 * TODO 不完善 路由守卫
 */
router.beforeEach(async (to) => {
    const authStore = useAuthStore()

    if (!authStore.token && to.path !== '/login') {
        return '/login'
    }
})


export default router