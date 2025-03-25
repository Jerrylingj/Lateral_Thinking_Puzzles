<!-- src/views/HomeView.vue -->
<template>
    <a-layout class="container">
      <a-layout-content class="content">
        <a-button type="primary" size="large" @click="startNewGame">点击开始</a-button>
      </a-layout-content>
    </a-layout>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router'

  const router = useRouter()
  
  const startNewGame = () => {
    // 生成合法long型ID（兼容前后端）
    const generateLongId = () => {
      const MAX_SAFE = 281474976710656 // 2^48
      const MIN_SAFE = 100000000000000 // 14位起始
      
      const timestamp = Date.now() % 1000000 // 取时间戳后6位
      const randomPart = Math.floor(Math.random() * (MAX_SAFE - MIN_SAFE)) + MIN_SAFE
      
      // 组合成16-17位数字
      return Number(`${timestamp}${randomPart}`.substring(0, 16))
    }
  
    const roomId = generateLongId()
    router.push(`/chat/${roomId}`)
  }

  </script>
  
  <style scoped>
  .container {
    height: 100vh;
  }
  .content {
    display: flex;
    justify-content: center;
    align-items: center;
    height: calc(100vh - 64px);
  }
  </style>