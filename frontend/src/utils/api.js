import axios from 'axios';
import envConfig from '../config/env';

// 根据配置决定使用哪个API基础URL
const getBaseUrl = () => {
  return envConfig.enableCloud ? envConfig.cloudApiUrl : envConfig.localApiUrl;
};

// 创建axios实例
const api = axios.create({
  baseURL: getBaseUrl(),
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 每次请求前重新获取baseURL，确保配置更改后立即生效
    config.baseURL = getBaseUrl();
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    console.error('API请求错误:', error);
    return Promise.reject(error);
  }
);

export default api;