import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import {createPinia} from "pinia";
import piniaPersist from 'pinia-plugin-persistedstate'
import router from '@/router/index'

const app = createApp(App)

// pinia
const pinia = createPinia()
pinia.use(piniaPersist)
app.use(pinia)

// element-plus
app.use(ElementPlus)

// vue-router
app.use(router)

app.mount('#app')