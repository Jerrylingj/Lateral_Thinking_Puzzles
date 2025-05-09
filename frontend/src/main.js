import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import axios from 'axios';

// 引入Element Plus
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

const app = createApp(App);
app.use(router);
app.use(Antd);
app.use(ElementPlus);

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

app.config.globalProperties.$axios = axios; // 全局挂载 axios
app.mount('#app');