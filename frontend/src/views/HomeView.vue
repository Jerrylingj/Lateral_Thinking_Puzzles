<!-- src/views/HomeView.vue -->
<template>
  <div class="home-container">
    <div class="grid-background"></div>
    <div class="content-wrapper">
      <div class="title-group">
        <h1 class="main-title">AI 海龟汤</h1>
        <p class="subtitle">结构主义理性之美的侧向思维推理游戏</p>
      </div>
      <div class="action-group">
        <button class="action-btn primary" @click="startGame">
          <el-icon><CaretRight /></el-icon>
          <span>开启新的推理</span>
        </button>
        <button class="action-btn secondary" @click="goToHistory">
          <el-icon><Document /></el-icon>
          <span>查阅历史归档</span>
        </button>
      </div>
    </div>
    <footer class="home-footer">
      <p>Designed with Structuralist Rationality Aesthetic</p>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { CaretRight, Document } from '@element-plus/icons-vue'

const router = useRouter()

const startGame = () => {
  // 使用时间戳，确保唯一性
  const roomId = Date.now()
  router.push(`/chat/${roomId}`)
}

const goToHistory = () => {
  router.push('/history')
}
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  width: 100vw;
  background-color: #FFFFFF;
  background-image: radial-gradient(circle at top left, rgba(0, 122, 255, 0.05), transparent 30%),
                    radial-gradient(circle at bottom right, rgba(0, 122, 255, 0.05), transparent 30%);
  color: #1a1a1a;
  overflow: hidden;
  position: relative;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}

.grid-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.04) 1px, transparent 1px);
  background-size: 40px 40px;
  opacity: 0.5;
}

.content-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 2rem;
  animation: fade-in-scale 1s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes fade-in-scale {
  from { 
    opacity: 0; 
    transform: translateY(20px) scale(0.98); 
  }
  to { 
    opacity: 1; 
    transform: translateY(0) scale(1); 
  }
}

.title-group {
  margin-bottom: 3.5rem;
}

.main-title {
  font-size: 4.5rem;
  font-weight: 700;
  letter-spacing: -0.03em;
  background: linear-gradient(120deg, #333, #000);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.subtitle {
  font-size: 1.2rem;
  color: #666666;
  margin-top: 0.75rem;
  font-weight: 400;
}

.action-group {
  display: flex;
  flex-direction: row;
  gap: 1.5rem;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  padding: 0 1.8rem;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 12px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  height: 52px;
}

.action-btn .el-icon {
  font-size: 1.2rem;
}

.action-btn.primary {
  background: #007AFF;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.15), 0 1px 3px rgba(0, 122, 255, 0.2);
}

.action-btn.primary:hover {
  transform: translateY(-3px);
  background: #0070e9;
  box-shadow: 0 7px 18px rgba(0, 122, 255, 0.2), 0 4px 5px rgba(0, 122, 255, 0.1);
}

.action-btn.primary:active {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.2);
}

.action-btn.secondary {
  background-color: #FFFFFF;
  color: #333;
  border: 1px solid #E5E5E5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04), 0 1px 3px rgba(0, 0, 0, 0.08);
}

.action-btn.secondary:hover {
  transform: translateY(-3px);
  border-color: #D1D1D1;
  box-shadow: 0 7px 18px rgba(0, 0, 0, 0.06), 0 4px 5px rgba(0, 0, 0, 0.08);
}

.action-btn.secondary:active {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.home-footer {
  position: absolute;
  bottom: 1.5rem;
  color: #AAAAAA;
  font-size: 0.8rem;
  z-index: 1;
}
</style>