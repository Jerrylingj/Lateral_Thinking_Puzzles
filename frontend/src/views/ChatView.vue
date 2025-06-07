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
          :class="[message.sender, {'is-typing-placeholder': message.isTyping}]"
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
             <img :src="AI_AVATAR" alt="avatar" />
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
        <button 
          class="send-btn" 
          @click="sendMessage" 
          :disabled="!inputMessage.trim() || isTyping"
        >
          <el-icon><Promotion /></el-icon>
        </button>
        <button class="end-game-btn" @click="handleGameEnd">
          结束
        </button>
      </div>
    </footer>
  </div>
</template>

<script setup>
  import { ref, reactive, onMounted, nextTick, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { ArrowLeft, Document, CaretRight, ChatLineSquare, CircleClose, Promotion } from '@element-plus/icons-vue'
  import axios from 'axios'

  const route = useRoute()
  
  const API_BASE = '/api/chat'
  
  // 强制转换roomId为数值类型
  const roomId = Number(route.params.roomId)

  const inputMessage = ref('')
  const isGameStarted = ref(false)
  const gameEnded = ref(false)
  const messageContainer = ref(null)
  const isTyping = ref(false)

  const messages = reactive([])

  const AI_AVATAR = '/ai-avatar.svg'
  const USER_AVATAR = '/user-avatar.svg'

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
      // 显示打字动画
      isTyping.value = true
      await scrollToBottom()
      
      const { data } = await axios.post(`${API_BASE}/rooms/${roomId}/messages`, { content: '开始游戏' })
      
      // 延迟一点以增强真实感
      setTimeout(() => {
        isTyping.value = false
        
        ElMessage({
          type: 'success',
          message: '游戏开始!'
        })

        addMessage('ai', data.content)
        isGameStarted.value = true
        
        // 滚动到底部显示新消息
        scrollToBottom()
      }, 1000)
    } catch (error) {
      isTyping.value = false
      ElMessage.error('游戏开始失败: ' + (error.message || '请检查网络'))
    }
  }

  // 游戏结束
  const handleGameEnd = async () => {
    try {
      ElMessageBox.confirm(
        '确定要结束当前游戏吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(async () => {
        // 显示打字动画
        isTyping.value = true
        await scrollToBottom()
        
        const { data } = await axios.post(`${API_BASE}/rooms/${roomId}/messages`, { content: '结束游戏' })
        
        // 延迟一点以增强真实感
        setTimeout(() => {
          isTyping.value = false
          
          addMessage('ai', data.content)
          checkGameEnd(data.content)
          
          scrollToBottom()
          ElMessage({
            type: 'success',
            message: '游戏已结束',
          })
        }, 1000)
      }).catch(() => {
        // 用户取消
      })
    } catch (error) {
      isTyping.value = false
      ElMessage.error('游戏结束失败')
      handleApiError(error)
    }
  }

  // 添加消息
  const addMessage = (sender, content) => {
    messages.push({
      sender,
      content,
      avatar: sender === 'ai' ? AI_AVATAR : USER_AVATAR,
      time: formatTime()
    })
  }

  // 检查游戏是否结束
  const checkGameEnd = (content) => {
    if (content.includes('【汤底揭晓】')) {
      gameEnded.value = true
    }
  }

  // 发送消息
  const sendMessage = async () => {
    const messageText = inputMessage.value.trim()
    if (!messageText || !isGameStarted.value || gameEnded.value) return
    
    try {
      // 先添加用户消息
      addMessage('user', messageText)
      await scrollToBottom()
      
      // 清空输入框
      inputMessage.value = ''
      
      // 显示打字动画
      isTyping.value = true
      await scrollToBottom()
      
      // 发送请求
      const { data } = await axios.post(`${API_BASE}/rooms/${roomId}/messages`, { content: messageText })
      
      // 延迟一点以增强真实感
      setTimeout(() => {
        // 关闭打字动画
        isTyping.value = false
        
        // 添加AI回复
        addMessage('ai', data.content)
        
        // 检查游戏是否结束
        checkGameEnd(data.content)
        
        // 滚动到底部
        scrollToBottom()
      }, 1000)
    } catch (error) {
      isTyping.value = false
      handleApiError(error)
    }
  }
  
  // 错误处理
  const handleApiError = (error) => {
    if (error.response) {
      const { status, data } = error.response
      ElMessage.error(`请求错误 (${status}): ${typeof data === 'string' ? data : JSON.stringify(data)}`)
    } else if (error.request) {
      ElMessage.error('网络连接异常，请检查服务器是否正常运行')
    } else {
      ElMessage.error(`发生错误: ${error.message}`)
    }
  }
  
  // 监听消息变化，自动滚动
  watch(messages, () => {
    scrollToBottom()
  })
  
  // 获取历史对话
  onMounted(async () => {
    try {
      // 尝试获取该房间的历史对话
      const { data } = await axios.get(`${API_BASE}/rooms/${roomId}`)
      
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
  background-color: #000000;
  color: #EAEAEA;
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
  padding: 1rem 1.5rem;
  z-index: 100;
  height: 65px;
  background-color: rgba(10, 10, 10, 0.7);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-btn {
  background: transparent;
  border: none;
  color: #AAAAAA;
  font-size: 1.5rem;
  cursor: pointer;
  transition: color 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
}

.header-btn:hover {
  color: #FFFFFF;
}

.room-info {
  text-align: center;
}

.room-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
  color: #FFFFFF;
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
  padding: 85px 1.5rem 20px 1.5rem; /* top padding for header */
  scrollbar-width: thin;
  scrollbar-color: #444 #111;
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
  height: calc(100vh - 200px);
  color: #777;
  text-align: center;
  animation: fade-in 1s ease;
}

.empty-icon-wrapper {
  font-size: 3rem;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  margin-bottom: 1.5rem;
  color: #888;
}

.empty-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #ddd;
}

.empty-subtitle {
  font-size: 1rem;
  color: #888;
  max-width: 300px;
}

/* Message Items */
.message-item {
  display: flex;
  gap: 0.8rem;
  max-width: 75%;
  align-items: flex-start;
  animation: slide-up 0.4s ease-out;
}

.message-item.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-item.ai {
  align-self: flex-start;
}

@keyframes slide-up {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.1);
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
  background: #1C1C1E;
  padding: 0.8rem 1.2rem;
  border-radius: 18px;
}

.message-item.user .message-content {
  background: linear-gradient(135deg, #0052D4, #4364F7, #6FB1FC);
  color: #FFFFFF;
  border-top-right-radius: 4px;
}

.message-item.ai .message-content {
  background: #2C2C2E;
  color: #EAEAEA;
  border-top-left-radius: 4px;
}

.message-text {
  font-size: 1rem;
  line-height: 1.6;
  white-space: pre-wrap;
  margin: 0;
}

.message-time {
  font-size: 0.75rem;
  color: #888;
  margin-top: 0.5rem;
  padding: 0 0.5rem;
}

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
  background-color: #888;
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
  padding: 1rem 1.5rem;
  background: linear-gradient(to top, rgba(0, 0, 0, 1), rgba(0, 0, 0, 0));
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
  background-color: #2C2C2E;
  color: #EAEAEA;
  border: 1px solid #444;
  border-radius: 12px;
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover:not(:disabled) {
  background-color: #3A3A3C;
  border-color: #555;
  transform: translateY(-2px);
}
.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.start-btn {
  background: #0052D4;
  border-color: #0052D4;
  color: white;
}
.end-btn-disabled {
  background: #444;
  border-color: #555;
  color: #999;
}


.input-area {
  display: flex;
  align-items: flex-end;
  gap: 0.8rem;
  background: #1C1C1E;
  padding: 0.6rem;
  border-radius: 16px;
  border: 1px solid #333;
  transition: border-color 0.2s ease;
}

.input-area:focus-within {
  border-color: #4364F7;
}

.message-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: #EAEAEA;
  font-size: 1rem;
  line-height: 1.5;
  resize: none;
  max-height: 120px;
  padding: 0.5rem;
  font-family: inherit;
}

.message-input::placeholder {
  color: #888;
}

.send-btn, .end-game-btn {
  flex-shrink: 0;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.send-btn {
  background-color: #0052D4;
  color: white;
  width: 48px;
  height: 48px;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
.send-btn:disabled {
  background-color: #333;
  color: #777;
  cursor: not-allowed;
}
.send-btn:not(:disabled):hover {
  background-color: #4364F7;
}

.end-game-btn {
  background-color: #444;
  color: #EAEAEA;
  height: 48px;
  padding: 0 1.2rem;
  font-size: 0.9rem;
}
.end-game-btn:hover {
  background-color: #D83A3A;
  color: white;
}
</style>