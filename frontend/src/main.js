import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import './assets/css/global.css'
import { inject as injectApi } from './api'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

injectApi({ store, router })

store.dispatch('favorite/load')
store.dispatch('notify/init')

app.use(store)
app.use(router)
app.use(ElementPlus)

app.mount('#app')
