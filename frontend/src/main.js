import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import axios from 'axios';
import api from './utils/api'; // 导入api工具

// 从env.js导入环境配置
const envConfig = require('./config/env');
// 设置环境配置到全局变量供api.js使用
window.__ENV_CONFIG__ = envConfig;
console.log('环境配置已加载:', envConfig);

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
app.config.globalProperties.$api = api; // 全局挂载 api 工具
app.mount('#app');