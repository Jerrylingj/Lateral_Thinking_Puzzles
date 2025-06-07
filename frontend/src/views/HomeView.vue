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
  // 使用时间戳和随机数组合，确保唯一性
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
  background-color: #0A0A0A;
  color: #EAEAEA;
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
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 30px 30px;
  animation: bg-pan 120s linear infinite;
  z-index: 0;
}

@keyframes bg-pan {
  from { background-position: 0 0; }
  to { background-position: -3000px -3000px; }
}

.content-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 2rem;
  animation: fade-in 1.5s ease-out;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.title-group {
  margin-bottom: 3rem;
}

.main-title {
  font-size: 4rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  background: -webkit-linear-gradient(45deg, #FFFFFF, #B8B8B8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.subtitle {
  font-size: 1.25rem;
  color: #888888;
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
  padding: 0.8rem 1.8rem;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 8px;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
  height: 52px;
}

.action-btn .el-icon {
  font-size: 1.2rem;
}

.action-btn.primary {
  background: linear-gradient(45deg, #2a75ff, #0d59e3);
  color: white;
  border-color: #2a75ff;
  box-shadow: 0 5px 15px rgba(42, 117, 255, 0.2);
}

.action-btn.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(42, 117, 255, 0.3);
}

.action-btn.secondary {
  background-color: rgba(255, 255, 255, 0.05);
  color: #EAEAEA;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.action-btn.secondary:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
}

.home-footer {
  position: absolute;
  bottom: 1.5rem;
  color: #555555;
  font-size: 0.8rem;
  z-index: 1;
}
</style>