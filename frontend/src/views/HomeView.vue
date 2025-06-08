<!-- src/views/HomeView.vue -->
<template>
  <div class="scroll-container">
    <section class="home-container fullscreen-section">
      <div class="grid-background"></div>
      <div class="content-wrapper">
        <div class="title-group">
          <h1 class="main-title">AI 海龟汤</h1>
          <p class="subtitle">你的思维，是唯一的破局点。</p>
        </div>
        <div class="action-group">
          <button class="action-btn primary" @click="startGame">
            <el-icon><CaretRight /></el-icon>
            <span>开始推理</span>
          </button>
          <button class="action-btn secondary" @click="goToHistory">
            <el-icon><Document /></el-icon>
            <span>历史归档</span>
          </button>
        </div>
      </div>
      <footer class="home-footer">
        <p>Designed with Structuralist Rationality Aesthetic</p>
      </footer>
      <div class="scroll-down-indicator">
        <el-icon><ArrowDownBold /></el-icon>
      </div>
    </section>

    <section class="info-screen fullscreen-section" ref="infoScreen" :class="{ 'is-visible': isInfoVisible }">
      <div class="section-content">
        <h2 class="section-title">一个故事，多种可能</h2>
        <p class="section-description">
          欢迎来到"海龟汤"的次世代。在这里, AI将成为你的专属出题人，为你讲述一个个看似简单却暗藏玄机的场景。你的任务，就是通过对话，揭开故事唯一的真相。
        </p>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon-wrapper"><el-icon><Share /></el-icon></div>
            <h3>无限谜题</h3>
            <p>由生成式 AI 匠心打造，确保您每次都能体验到前所未有的新奇挑战，让您的脑洞大开。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper"><el-icon><Cpu /></el-icon></div>
            <h3>智能法官</h3>
            <p>我们的人工智能不仅能精准判断您的提问，还会在您陷入僵局时，给予恰到好处的智能提示。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper"><el-icon><MagicStick /></el-icon></div>
            <h3>沉浸式体验</h3>
            <p>我们专注于核心玩法，为您呈现一个简约而不简单的界面，让您能够完全沉浸在纯粹的推理乐趣中。</p>
          </div>
        </div>
      </div>
    </section>

    <section class="final-cta-screen fullscreen-section" ref="finalCtaScreen" :class="{ 'is-visible': isFinalCtaVisible }">
      <div class="section-content">
        <h2 class="section-title cta-title">准备好迎接挑战了吗？</h2>
        <p class="section-description cta-description">
          谜底，只为有准备的头脑揭晓。立即开启你的第一桩案件，探寻线索背后的真相吧。
        </p>
        <button class="action-btn primary large" @click="startGame">
          <el-icon><CaretRight /></el-icon>
          <span>开始推理之旅！</span>
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { CaretRight, Document, ArrowDownBold, Share, Cpu, MagicStick } from '@element-plus/icons-vue'

const router = useRouter()

const startGame = () => {
  // 使用时间戳，确保唯一性
  const roomId = Date.now()
  router.push(`/chat/${roomId}`)
}

const goToHistory = () => {
  router.push('/history')
}

const infoScreen = ref(null)
const isInfoVisible = ref(false)
const finalCtaScreen = ref(null)
const isFinalCtaVisible = ref(false)

let observer

onMounted(() => {
  const options = {
    root: null,
    rootMargin: '0px',
    threshold: 0.4
  }

  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.target === infoScreen.value) {
        isInfoVisible.value = entry.isIntersecting;
      } else if (entry.target === finalCtaScreen.value) {
        isFinalCtaVisible.value = entry.isIntersecting;
      }
    })
  }, options)

  if (infoScreen.value) observer.observe(infoScreen.value)
  if (finalCtaScreen.value) observer.observe(finalCtaScreen.value)
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped>
/* --- 基础与滚动容器 --- */
.scroll-container {
  height: 100vh;
  overflow-y: auto;
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth;
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

.scroll-container::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}

.fullscreen-section {
  height: 100vh;
  width: 100vw;
  scroll-snap-align: start;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}


/* --- 模块: 首页 --- */
.home-container {
  background-color: #FFFFFF;
  color: #1a1a1a;
}

.home-container::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  z-index: 0;
  background:
    radial-gradient(circle at 15% 25%, rgba(0, 255, 255, 0.25), transparent 32%),
    radial-gradient(circle at 85% 35%, rgba(0, 122, 255, 0.25), transparent 38%),
    radial-gradient(circle at 35% 85%, rgba(138, 43, 226, 0.25), transparent 32%),
    radial-gradient(circle at 90% 90%, rgba(0, 255, 127, 0.2), transparent 38%);
  animation: float-gradients 25s linear infinite;
  will-change: transform;
}

.grid-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.03) 1px, transparent 1px),
    linear-gradient(rgba(0, 0, 0, 0.02) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.02) 1px, transparent 1px);
  background-size: 40px 40px, 40px 40px, 120px 120px, 120px 120px;
  opacity: 0.6;
  animation: pan-grid 90s linear infinite;
}

.content-wrapper {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 2rem;
  animation: fade-in-scale 1s cubic-bezier(0.16, 1, 0.3, 1) forwards;
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

.action-btn.large {
  height: 60px;
  padding: 0 2.5rem;
  font-size: 1.1rem;
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
  z-index: 2;
}

.scroll-down-indicator {
  position: absolute;
  bottom: 2rem;
  color: #c0c0c0;
  z-index: 2;
  animation: subtle-pulse 2.5s infinite ease-in-out;
  font-size: 1.5rem;
}


/* --- 模块: 信息介绍 --- */
.info-screen {
  background-color: #000;
  color: #fff;
  position: relative;
}

.info-screen::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 60px 60px;
  opacity: 0.3;
  z-index: 0;
}

.section-content {
  max-width: 900px;
  text-align: center;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 1;
}

.section-title {
  font-size: 3.5rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin-bottom: 1.5rem;
  background: linear-gradient(120deg, #e0e0e0, #ffffff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-description {
  font-size: 1.15rem;
  color: #a1a1a6;
  line-height: 1.7;
  max-width: 640px;
  margin-bottom: 5rem;
}

/* 特性卡片 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2.5rem;
  width: 100%;
}

.feature-card {
  background: #111;
  border-radius: 20px;
  padding: 2.5rem 2rem;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1),
              box-shadow 0.3s cubic-bezier(0.16, 1, 0.3, 1),
              border-color 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-10px);
  border-color: rgba(0, 122, 255, 0.5);
  box-shadow: 0 12px 24px rgba(0, 122, 255, 0.1), 0 6px 10px rgba(0, 122, 255, 0.08);
}

.feature-card:hover .feature-icon-wrapper {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 122, 255, 0.4);
}

.feature-icon-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #007aff, #0056b3);
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 15px rgba(0, 122, 255, 0.3);
  transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.feature-card .el-icon {
  font-size: 1.8rem;
  color: #fff;
}

.feature-card h3 {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #f5f5f7;
}

.feature-card p {
  font-size: 1rem;
  color: #8e8e93;
  line-height: 1.6;
}


/* --- 模块: 最终行动号召 --- */
.final-cta-screen {
  background-color: #FFFFFF;
  color: #1d1d1f;
}

.cta-title {
  background: linear-gradient(120deg, #333, #000);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.cta-description {
  color: #6e6e73;
  margin-bottom: 3rem;
}


/* --- 动画效果 --- */
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

@keyframes float-gradients {
  0% { transform: rotate(0deg); }
  50% { transform: rotate(180deg); }
  100% { transform: rotate(360deg); }
}

@keyframes pan-grid {
  from {
    background-position: 0 0;
  }
  to {
    background-position: -120px -120px;
  }
}

@keyframes subtle-pulse {
  0%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  50% {
    transform: translateY(10px);
    opacity: 1;
  }
}

/* 入场动画 */
.info-screen .section-content > *,
.info-screen .feature-card,
.final-cta-screen .section-content > * {
  opacity: 0;
  transform: translateY(40px);
  transition: opacity 1s cubic-bezier(0.16, 1, 0.3, 1), transform 1s cubic-bezier(0.16, 1, 0.3, 1);
}

.info-screen.is-visible .section-content > *,
.info-screen.is-visible .feature-card,
.final-cta-screen.is-visible .section-content > * {
  opacity: 1;
  transform: translateY(0);
}

.info-screen.is-visible .section-description,
.final-cta-screen.is-visible .cta-description {
  transition-delay: 0.1s;
}

.info-screen.is-visible .features-grid {
  transition-delay: 0.2s;
}

.final-cta-screen.is-visible .action-btn {
  transition-delay: 0.2s;
}

.info-screen.is-visible .feature-card:nth-child(1) {
  transition-delay: 0.3s;
}
.info-screen.is-visible .feature-card:nth-child(2) {
  transition-delay: 0.45s;
}
.info-screen.is-visible .feature-card:nth-child(3) {
  transition-delay: 0.6s;
}


/* --- 响应式布局 (手机端) --- */
@media (max-width: 768px) {
  .fullscreen-section {
    height: auto;
    min-height: 100vh;
    padding: 4rem 0;
  }

  .scroll-container {
    overflow-y: auto;
  }

  .content-wrapper, .section-content {
    padding: 2rem 1.5rem;
  }

  .main-title {
    font-size: 3rem;
  }

  .subtitle {
    font-size: 1rem;
  }

  .action-group {
    flex-direction: column;
    gap: 1rem;
    width: 100%;
    padding: 0 1rem;
  }

  .section-title {
    font-size: 2.5rem;
  }

  .section-description {
    font-size: 1rem;
    margin-bottom: 4rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .scroll-down-indicator {
    display: none;
  }

  .home-container::before {
    animation-duration: 40s;
  }
}
</style>