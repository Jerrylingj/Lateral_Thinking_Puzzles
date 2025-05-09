<template>
  <a-config-provider :theme="theme">
    <div class="app-container">
      <router-view />
    </div>
  </a-config-provider>
</template>

<script setup>
import { ref } from 'vue'

// 全局主题配置
const theme = ref({
  token: {
    colorPrimary: '#6B8DD6',
    colorSuccess: '#4CAF50',
    colorWarning: '#FF9800',
    colorError: '#F44336',
    colorInfo: '#2196F3',
    borderRadius: 8,
    wireframe: false,
  },
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  width: 100%;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: #f6f8fa;
  color: #333;
  overflow: hidden;
}

#app, .app-container {
  height: 100vh;
  width: 100%;
}

/* 统一滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: rgba(107, 141, 214, 0.5);
  border-radius: 4px;
  transition: all 0.3s;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(107, 141, 214, 0.8);
}

/* 输入框聚焦时的效果 */
input:focus, textarea:focus, button:focus {
  outline: none;
}

/* 平滑过渡 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* 动画效果 */
@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}
</style>