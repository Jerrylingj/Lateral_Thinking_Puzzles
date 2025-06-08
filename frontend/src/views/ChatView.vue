<!-- src/views/ChatView.vue -->
<template>
  <div class="chat-view">
    <!-- 顶部导航栏 -->
    <header class="chat-header">
      <button class="header-btn back-btn" @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <div class="room-info">
        <h1 class="room-title">推理房间</h1>
        <span class="room-id">ID: {{ roomId }}</span>
      </div>
      <button class="header-btn history-btn" @click="$router.push('/history')">
        <el-icon><Document /></el-icon>
      </button>
    </header>

    <!-- 消息主体区域 -->
    <main class="chat-main" ref="messageContainer">
      <div class="message-list">
        <template v-if="messages.length === 0 && !isGameStarted">
          <div class="empty-chat-state">
            <div class="empty-icon-wrapper">
              <el-icon><ChatLineSquare /></el-icon>
            </div>
            <h2 class="empty-title">谜题尚未展开</h2>
            <p class="empty-subtitle">点击下方的"开始游戏"，启动思维的引擎</p>
          </div>
        </template>
        
        <div 
          v-for="(message, index) in messages" 
          :key="index" 
          class="message-item"
          :class="message.sender"
        >
          <div class="message-avatar">
             <img :src="message.avatar" alt="avatar" />
          </div>
          <div class="message-content-wrapper">
            <div class="message-content">
              <p class="message-text">{{ message.content }}</p>
            </div>
            <span class="message-time">{{ message.time }}</span>
          </div>
        </div>
        
        <div class="message-item ai" v-if="isTyping">
           <div class="message-avatar">
             <img :src="AI_AVATAR_SVG" alt="avatar" />
          </div>
           <div class="message-content-wrapper">
             <div class="message-content typing-indicator">
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
             </div>
           </div>
        </div>
      </div>
    </main>

    <!-- 底部控制区域 -->
    <footer class="chat-footer">
      <div class="action-buttons" v-if="!isGameStarted || gameEnded">
         <button class="action-btn start-btn" @click="handleGameStart" :disabled="isGameStarted">
          <el-icon><CaretRight /></el-icon>
          开始新游戏
        </button>
        <button v-if="gameEnded" class="action-btn end-btn-disabled" disabled>
          <el-icon><CircleClose /></el-icon>
          本轮已结束
        </button>
      </div>

      <div class="input-area" v-else>
        <textarea
          ref="inputRef"
          v-model="inputMessage"
          placeholder="提出你的问题..."
          class="message-input"
          @keypress.enter.prevent="sendMessage"
          @input="adjustTextareaHeight"
        ></textarea>
        <button class="end-game-btn" @click="handleGameEnd" title="结束游戏">
          <el-icon><SwitchButton /></el-icon>
        </button>
        <button 
          class="send-btn" 
          @click="sendMessage" 
          :disabled="!inputMessage.trim() || isTyping"
          title="发送"
        >
          <el-icon><Promotion /></el-icon>
        </button>
      </div>
    </footer>
  </div>
</template>

<script setup>
  import { ref, reactive, onMounted, nextTick, watch, getCurrentInstance } from 'vue'
  import { useRoute } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { ArrowLeft, Document, CaretRight, ChatLineSquare, CircleClose, Promotion, SwitchButton } from '@element-plus/icons-vue'
  import { getUserId } from '@/utils/user'

  const route = useRoute()
  const { proxy } = getCurrentInstance()
  
  const API_BASE = '/api/chat'
  
  // 强制转换roomId为数值类型
  const roomId = Number(route.params.roomId)
  const userId = getUserId()

  const inputMessage = ref('')
  const isGameStarted = ref(false)
  const gameEnded = ref(false)
  const messageContainer = ref(null)
  const isTyping = ref(false)

  const messages = reactive([])

  const AI_AVATAR_SVG = `data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><g fill="none" stroke="%23007AFF" stroke-width="5"><path d="M25,35 A25,25 0 1,1 75,35" stroke-linecap="round" /><path d="M25,35 A25,25 0 0,0 75,35" stroke-linecap="round"/><circle cx="50" cy="50" r="45" /></g></svg>`
  const USER_AVATAR_SVG = `data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><g fill="none" stroke="%23333" stroke-width="5"><circle cx="50" cy="35" r="15" /><path d="M15,90 A35,35 0 0,1 85,90" stroke-linecap="round"/></g></svg>`

  // 格式化当前时间
  const formatTime = () => {
    const now = new Date()
    return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
  }

  // 自动滚动到底部
  const scrollToBottom = async () => {
    await nextTick()
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  }

  // 游戏开始
  const handleGameStart = async () => {
    try {
      isTyping.value = true
      await scrollToBottom()
      
      const requestBody = { userId, message: '开始游戏' };
      const data = await proxy.$api.post(`${API_BASE}/${roomId}/send`, requestBody)
      
      setTimeout(() => {
        isTyping.value = false
        ElMessage({ type: 'success', message: '游戏开始!' })
        addMessage('ai', data)
        isGameStarted.value = true
        scrollToBottom()
      }, 800)
    } catch (error) {
      isTyping.value = false
      ElMessage.error('游戏开始失败: ' + (error.message || '请检查网络'))
    }
  }

  // 游戏结束
  const handleGameEnd = async () => {
    ElMessageBox.confirm('确定要结束当前游戏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      isTyping.value = true
      await scrollToBottom()
      
      const requestBody = { userId, message: '结束游戏' };
      const data = await proxy.$api.post(`${API_BASE}/${roomId}/send`, requestBody)
      
      setTimeout(() => {
        isTyping.value = false
        addMessage('ai', data)
        checkGameEnd(data)
        scrollToBottom()
        ElMessage({ type: 'success', message: '游戏已结束' })
      }, 800)
    }).catch(() => { /* User cancelled */ })
  }

  // 添加消息
  const addMessage = (sender, content) => {
    messages.push({
      sender,
      content,
      avatar: sender === 'ai' ? AI_AVATAR_SVG : USER_AVATAR_SVG,
      time: formatTime()
    })
  }

  // 检查游戏是否结束
  const checkGameEnd = (content) => {
    if (typeof content === 'string' && content.includes('【汤底揭晓】')) {
      gameEnded.value = true
    }
  }

  // 发送消息
  const sendMessage = async () => {
    const messageText = inputMessage.value.trim()
    if (!messageText || !isGameStarted.value || gameEnded.value) return
    
    addMessage('user', messageText)
    inputMessage.value = ''
    await scrollToBottom()
    
    isTyping.value = true
    await scrollToBottom()

    try {
      const requestBody = { userId, message: messageText };
      const data = await proxy.$api.post(`${API_BASE}/${roomId}/send`, requestBody);
      
      setTimeout(() => {
        isTyping.value = false
        addMessage('ai', data)
        checkGameEnd(data)
        scrollToBottom()
      }, 800)
    } catch (error) {
      isTyping.value = false
      handleApiError(error)
    }
  }
  
  // 错误处理
  const handleApiError = (error) => {
    const message = error.response?.data?.message || error.message || '未知错误'
    ElMessage.error(`请求错误: ${message}`)
  }
  
  // 监听消息变化，自动滚动
  watch(messages, () => {
    scrollToBottom()
  })
  
  // 获取历史对话
  onMounted(async () => {
    try {
      // 尝试获取该房间的历史对话 - 修正：使用正确的历史记录接口
      const data = await proxy.$api.get(`${API_BASE}/history`, {
        params: { roomId }
      })
      
      // 如果存在历史记录
      if (data && data.chatMessageList && data.chatMessageList.length > 0) {
        // 添加历史消息到界面
        data.chatMessageList.forEach(msg => {
          addMessage(msg.role === 'ASSISTANT' ? 'ai' : 'user', msg.content)
        })
        
        // 检查游戏状态
        isGameStarted.value = true
        if (data.status === 'FINISHED') {
          gameEnded.value = true
        }
      }
      
      // 滚动到底部
      await scrollToBottom()
    } catch (error) {
      // 如果没有历史记录或发生错误，不做特殊处理
      console.log('No history found or error occurred:', error)
      handleApiError(error)
    }
  })
</script>

<style scoped>
/* Base Structure */
.chat-view {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  background-color: #FFFFFF;
  color: #1a1a1a;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  overflow: hidden;
}

/* Header */
.chat-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1rem;
  z-index: 100;
  height: 65px;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: saturate(180%) blur(10px);
  -webkit-backdrop-filter: saturate(180%) blur(10px);
  border-bottom: 1px solid #E5E5E5;
  transition: box-shadow 0.2s ease;
}

.header-btn {
  background: transparent;
  border: none;
  color: #666666;
  font-size: 1.5rem;
  cursor: pointer;
  transition: color 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
}

.header-btn:hover { color: #000000; }

.room-info {
  text-align: center;
}

.room-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
}

.room-id {
  font-size: 0.8rem;
  color: #888888;
  font-family: monospace;
}

/* Main Chat Area */
.chat-main {
  flex: 1;
  overflow-y: auto;
  padding: 85px 1.5rem 120px 1.5rem; /* top & bottom padding */
}

.chat-main::-webkit-scrollbar {
  width: 6px;
}

.chat-main::-webkit-scrollbar-track {
  background: #111;
}

.chat-main::-webkit-scrollbar-thumb {
  background-color: #444;
  border-radius: 6px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 1.8rem;
}

/* Empty State */
.empty-chat-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 10vh;
  color: #999;
  text-align: center;
  animation: fade-in 1s ease;
}

.empty-icon-wrapper {
  font-size: 2.5rem;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F0F0F0;
  margin-bottom: 1.5rem;
  color: #BDBDBD;
}

.empty-title {
  font-size: 1.4rem;
  font-weight: 500;
  color: #555;
}

.empty-subtitle {
  font-size: 0.9rem;
  color: #999;
  max-width: 280px;
}

/* Message Items */
.message-item {
  display: flex;
  gap: 0.8rem;
  max-width: 75%;
  align-items: flex-start;
  animation: slide-up-fade 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.message-item.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-item.ai {
  align-self: flex-start;
}

@keyframes slide-up-fade {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.message-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background-color: #F0F0F0;
  padding: 5px;
}

.message-avatar img {
  width: 100%;
  height: 100%;
}

.message-content-wrapper {
    display: flex;
    flex-direction: column;
}

.message-item.user .message-content-wrapper {
    align-items: flex-end;
}

.message-item.ai .message-content-wrapper {
    align-items: flex-start;
}

.message-content {
  padding: 0.8rem 1.2rem;
  border-radius: 20px;
}

.message-item.user .message-content {
  background: #007AFF;
  color: #FFFFFF;
  border-bottom-right-radius: 4px;
}

.message-item.ai .message-content {
  background: #EFEFEF;
  color: #1a1a1a;
  border-bottom-left-radius: 4px;
}

.message-text {
  font-size: 1rem;
  line-height: 1.6;
  white-space: pre-wrap;
  margin: 0;
}

.message-time {
  font-size: 0.75rem;
  color: #AAAAAA;
  margin-top: 0.5rem;
  padding: 0 0.5rem;
}

.message-item.user .message-time { color: rgba(255, 255, 255, 0.7); }

/* Typing Indicator */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 12px 15px;
}

.typing-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #BDBDBD;
  animation: typing-bounce 1.4s infinite ease-in-out both;
}
.typing-dot:nth-child(1) { animation-delay: -0.32s; }
.typing-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing-bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}


/* Footer */
.chat-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 1rem 1.5rem 1.5rem;
  background: linear-gradient(to top, rgba(255, 255, 255, 1), rgba(255, 255, 255, 0));
  z-index: 100;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background-color: #FFFFFF;
  color: #333;
  border: 1px solid #E5E5E5;
  border-radius: 12px;
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.action-btn:hover:not(:disabled) {
  background-color: #F9F9F9;
  border-color: #DCDCDC;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.start-btn {
  background: #007AFF;
  border-color: #007AFF;
  color: white;
}
.start-btn:hover { background: #0070e9 !important; border-color: #0070e9 !important; }
.end-btn-disabled {
  background: #E5E5E5;
  border-color: #E5E5E5;
  color: #999;
}


.input-area {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  background: #FFFFFF;
  padding: 0.5rem;
  border-radius: 24px;
  border: 1px solid #E5E5E5;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.07);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.input-area:focus-within {
  border-color: #007AFF;
  box-shadow: 0 4px 20px rgba(0, 122, 255, 0.15);
}

.message-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: #1a1a1a;
  font-size: 1rem;
  line-height: 1.5;
  resize: none;
  max-height: 120px;
  padding: 0.6rem 1rem;
  font-family: inherit;
}

.message-input::placeholder {
  color: #AAAAAA;
}

.send-btn, .end-game-btn {
  flex-shrink: 0;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.3rem;
}

.send-btn {
  background-color: #007AFF;
  color: white;
}
.send-btn:disabled {
  background-color: #E5E5E5;
  color: #BDBDBD;
  cursor: not-allowed;
}
.send-btn:not(:disabled):hover {
  background-color: #0070e9;
  transform: scale(1.05);
}

.end-game-btn {
  background-color: #F0F0F0;
  color: #555;
}
.end-game-btn:hover {
  background-color: #E5E5E5;
}
</style>