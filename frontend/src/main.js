import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import axios from 'axios';

const app = createApp(App);
app.use(router);
app.use(Antd);
app.config.globalProperties.$axios = axios; // 全局挂载 axios
app.mount('#app');