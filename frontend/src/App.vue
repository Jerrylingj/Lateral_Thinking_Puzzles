<template>
  <a-config-provider :theme="theme">
    <a-layout class="app-container">
      <!-- 顶部导航 -->
      <a-layout-header class="app-header">
        <h1 class="app-title">AI 海龟汤</h1>
      </a-layout-header>

      <!-- 主要内容区域 -->
      <a-layout-content class="main-content">
        <router-view />
      </a-layout-content>

      <!-- 底部信息 -->
      <a-layout-footer class="app-footer">
        © 2024 AI海龟汤 版权所有
      </a-layout-footer>
    </a-layout>
  </a-config-provider>
</template>

<script setup>
/* import { theme } from 'ant-design-vue/es/default-theme'

// 全局主题配置
const theme = {
  token: {
    colorPrimary: '#1890ff',
    borderRadius: 8,
  },
} */
</script>

<style scoped>
.app-container {
  min-height: 100vh;
}

.app-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 24px;
}

.app-title {
  color: rgba(0, 0, 0, 0.85);
  font-size: 1.5rem;
  margin: 0;
  line-height: 64px;
}

.main-content {
  padding: 24px;
  background: #f5f5f5;
  min-height: calc(100vh - 128px);
}

.app-footer {
  text-align: center;
  background: #fff;
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
}
</style>

<style>
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>