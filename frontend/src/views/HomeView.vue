<!-- src/views/HomeView.vue -->
<template>
  <div class="home-container">
    <div class="animated-bg">
      <div class="bubble bubble-1"></div>
      <div class="bubble bubble-2"></div>
      <div class="bubble bubble-3"></div>
      <div class="bubble bubble-4"></div>
    </div>
    <div class="content-wrapper">
      <div class="title-container">
        <h1 class="main-title">AI 海龟汤</h1>
        <p class="subtitle">探索谜题，激发思维</p>
      </div>
      <el-button 
        type="primary" 
        size="large" 
        class="start-button" 
        @click="startNewGame"
      >
        <el-icon class="start-icon"><CaretRight /></el-icon>
        点击开始
      </el-button>
      <div class="history-link" @click="viewHistory">
        <el-icon><Document /></el-icon>
        <span>往期对话记录</span>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { useRouter } from 'vue-router'
import { CaretRight, Document } from '@element-plus/icons-vue'

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

const viewHistory = () => {
  router.push('/history')
}
</script>
  
<style scoped>
.home-container {
  height: 100vh;
  background: linear-gradient(135deg, #409eff, #337ecc);
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
}

.animated-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.bubble {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(1px);
  animation: float 15s infinite ease-in-out;
}

.bubble-1 {
  width: 200px;
  height: 200px;
  left: 10%;
  top: 10%;
  animation-delay: 0s;
}

.bubble-2 {
  width: 300px;
  height: 300px;
  right: 15%;
  top: 20%;
  animation-delay: 2s;
}

.bubble-3 {
  width: 150px;
  height: 150px;
  left: 20%;
  bottom: 15%;
  animation-delay: 4s;
}

.bubble-4 {
  width: 250px;
  height: 250px;
  right: 10%;
  bottom: 10%;
  animation-delay: 6s;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(15px);
  border-radius: 20px;
  padding: 3.5rem 4.5rem;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
  max-width: 90%;
  animation: fadeIn 1s ease-out;
  position: relative;
  z-index: 10;
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.title-container {
  text-align: center;
  margin-bottom: 3.5rem;
}

.main-title {
  font-size: 4rem;
  font-weight: 800;
  color: #333;
  margin-bottom: 0.8rem;
  background: linear-gradient(90deg, #409eff, #337ecc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 3px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  animation: titlePulse 3s infinite alternate;
}

.subtitle {
  font-size: 1.4rem;
  color: #555;
  margin-top: 0.5rem;
  letter-spacing: 1px;
}

.start-button {
  height: auto;
  font-size: 1.3rem;
  padding: 0.9rem 2.8rem;
  border-radius: 50px;
  border: none;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.5);
  transition: all 0.3s;
  font-weight: 600;
  letter-spacing: 1px;
}

.start-icon {
  font-size: 1.3rem;
  margin-right: 4px;
}

.start-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 12px 25px rgba(64, 158, 255, 0.6);
}

.start-button:active {
  transform: translateY(1px);
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.4);
}

.history-link {
  margin-top: 2.5rem;
  color: #409eff;
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 1.1rem;
  transition: all 0.3s;
  padding: 0.8rem 1.5rem;
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.5);
}

.history-link:hover {
  color: #337ecc;
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.3);
}

.history-link span {
  margin-left: 0.5rem;
}

@keyframes fadeIn {
  0% { opacity: 0; transform: translateY(30px); }
  100% { opacity: 1; transform: translateY(0); }
}

@keyframes float {
  0% { transform: translateY(0) translateX(0) rotate(0); opacity: 0.6; }
  50% { transform: translateY(-40px) translateX(20px) rotate(180deg); opacity: 0.8; }
  100% { transform: translateY(0) translateX(0) rotate(360deg); opacity: 0.6; }
}

@keyframes titlePulse {
  0% { opacity: 0.9; transform: scale(1); }
  100% { opacity: 1; transform: scale(1.03); }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 2.5rem 2rem;
  }
  
  .main-title {
    font-size: 2.8rem;
  }
  
  .subtitle {
    font-size: 1.1rem;
  }
  
  .start-button {
    font-size: 1.1rem;
    padding: 0.8rem 2.2rem;
  }
}
</style>