<!-- src/views/ChatView.vue -->
<template>
  <div class="chat-container">
    <!-- 顶部导航栏 -->
    <div class="chat-header">
      <el-button 
        type="text" 
        class="back-button" 
        @click="$router.push('/')"
      >
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h1 class="room-id">房间号：{{ roomId }}</h1>
      <el-button 
        type="text" 
        class="history-button" 
        @click="$router.push('/history')"
      >
        <el-icon><Document /></el-icon>
      </el-button>
    </div>

    <!-- 消息主体区域 -->
    <div class="chat-main" ref="chatMain">
      <div class="message-scroll-container" ref="messageContainer">
        <div class="message-container">
          <template v-if="messages.length === 0">
            <div class="empty-chat">
              <div class="empty-illustration">
                <el-icon class="empty-icon"><ChatLineSquare /></el-icon>
              </div>
              <h3 class="empty-title">准备探索未知谜题</h3>
              <p class="empty-hint">点击下方"开始游戏"按钮，开启推理之旅</p>
            </div>
          </template>
          <div 
            v-for="(message, index) in messages" 
            :key="index" 
            class="message"
            :class="[message.sender, {'message-in': true}]"
          >
            <el-avatar 
              :src="message.avatar" 
              :size="42" 
              class="message-avatar"
            />
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部控制区域 -->
    <div class="chat-footer">
      <div class="controls">
        <el-button 
          type="success" 
          :disabled="isGameStarted"
          @click="handleGameStart"
          class="control-button start-button"
          :icon="CaretRight"
          size="large"
          round
        >
          开始游戏
        </el-button>
        <el-button 
          type="danger" 
          :disabled="gameEnded"
          @click="handleGameEnd"
          class="control-button end-button"
          :icon="Close"
          size="large"
          round
        >
          结束游戏
        </el-button>
      </div>

      <div class="input-container">
        <div class="input-wrapper">
          <el-input
            v-model="inputMessage"
            type="textarea"
            placeholder="输入你的猜测..."
            :autosize="{ minRows: 1, maxRows: 4 }"
            class="message-input"
            @keypress.enter.prevent="sendMessage"
            :disabled="!isGameStarted || gameEnded"
            clearable
          />
          <div class="send-button-wrapper">
            <el-button 
              type="primary" 
              @click="sendMessage" 
              class="send-button"
              :disabled="!isGameStarted || gameEnded || !inputMessage.trim()"
              circle
            >
              <i class="custom-send-icon">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                  <path d="M1.946 9.315c-.522-.174-.527-.455.01-.634l19.087-6.362c.529-.176.832.12.684.638l-5.454 19.086c-.15.529-.455.547-.679.045L12 14l6-8-8 6-8.054-2.685z"></path>
                </svg>
              </i>
            </el-button>
          </div>
        </div>
        <div class="input-hint" v-if="isGameStarted && !gameEnded">
          <el-icon><Light /></el-icon>
          <span>提示：尝试寻找线索，提出精确问题</span>
        </div>
      </div>
    </div>

    <!-- 打字动画效果 -->
    <div class="typing-indicator" v-if="isTyping">
      <div class="typing-dot"></div>
      <div class="typing-dot"></div>
      <div class="typing-dot"></div>
    </div>

    <!-- 全局消息通知 -->
    <el-backtop :right="20" :bottom="20" />
  </div>
</template>

<script setup>
  import { ref, reactive, onMounted, nextTick, watch } from 'vue'
  import { useRoute } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { ArrowLeft, Document, CaretRight, Close, Light, ChatLineSquare } from '@element-plus/icons-vue'
  import axios from 'axios'

  const API_BASE = 'http://localhost:3000/api/chat'
  const route = useRoute()
  
  // 强制转换roomId为数值类型
  const roomId = Number(route.params.roomId)

  const inputMessage = ref('')
  const isGameStarted = ref(false)
  const gameEnded = ref(false)
  const messageContainer = ref(null)
  const chatMain = ref(null)
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
      
      const response = await axios.post(`${API_BASE}/${roomId}/send?message=开始游戏`)
      
      // 延迟一点以增强真实感
      setTimeout(() => {
        isTyping.value = false
        
        ElMessage({
          type: 'success',
          message: '游戏开始!'
        })

        addMessage('ai', response.data)
        isGameStarted.value = true
        
        // 滚动到底部显示新消息
        scrollToBottom()
      }, 1000)
    } catch (error) {
      isTyping.value = false
      ElMessage.error('游戏开始失败')
      handleApiError(error)
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
        
        const response = await axios.post(`${API_BASE}/${roomId}/send?message=结束游戏`)
        
        // 延迟一点以增强真实感
        setTimeout(() => {
          isTyping.value = false
          
          addMessage('ai', response.data)
          checkGameEnd(response.data)
          
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
    if (content.includes('游戏已结束')) {
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
      
      // 使用URL参数确保消息正确编码
      const params = new URLSearchParams()
      params.append('message', messageText)
  
      // 发送请求
      const response = await axios.post(
        `${API_BASE}/${roomId}/send?${params.toString()}`,
        null,
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        }
      )
      
      // 延迟一点以增强真实感
      setTimeout(() => {
        // 关闭打字动画
        isTyping.value = false
        
        // 添加AI回复
        addMessage('ai', response.data)
        
        // 检查游戏是否结束
        checkGameEnd(response.data)
        
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
      const { data } = await axios.get(`${API_BASE}/history`, {
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
        const lastMessage = data.chatMessageList[data.chatMessageList.length - 1]
        if (lastMessage && lastMessage.content.includes('游戏已结束')) {
          gameEnded.value = true
        }
      }
      
      // 滚动到底部
      await scrollToBottom()
    } catch (error) {
      // 如果没有历史记录或发生错误，不做特殊处理
      console.log('No history found or error occurred:', error)
    }
  })
</script>

<style scoped>
  /* 基础样式 */
  .chat-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
    background: linear-gradient(to bottom, #f6f9fe, #eef2f9);
    font-family: 'Segoe UI', system-ui, sans-serif;
    position: relative;
  }
  
  /* 顶部导航栏 */
  .chat-header {
    background: linear-gradient(135deg, #409eff, #337ecc);
    color: white;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    display: flex;
    align-items: center;
    padding: 1rem 1.5rem;
    position: relative;
    z-index: 10;
  }
  
  .back-button, .history-button {
    color: white;
    font-size: 1.2rem;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 40px;
    width: 40px;
    border-radius: 50%;
    transition: all 0.3s ease;
  }
  
  .back-button:hover, .history-button:hover {
    background: rgba(255,255,255,0.2);
    transform: scale(1.1);
  }
  
  .room-id {
    flex: 1;
    font-size: 1.3rem;
    font-weight: 600;
    text-align: center;
    margin: 0;
    color: white;
    letter-spacing: 1px;
  }
  
  /* 消息主体区域 */
  .chat-main {
    flex: 1;
    overflow: hidden;
    position: relative;
    padding: 0.5rem 0;
  }
  
  .message-scroll-container {
    height: 100%;
    overflow-y: auto;
    padding: 1.5rem 1rem;
    scroll-behavior: smooth;
    scrollbar-width: thin;
    scrollbar-color: #c0c4cc #f4f4f5;
  }
  
  .message-scroll-container::-webkit-scrollbar {
    width: 6px;
  }
  
  .message-scroll-container::-webkit-scrollbar-track {
    background: #f4f4f5;
    border-radius: 10px;
  }
  
  .message-scroll-container::-webkit-scrollbar-thumb {
    background-color: #c0c4cc;
    border-radius: 10px;
  }
  
  .message-container {
    max-width: 90%;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  
  /* 空聊天室提示 */
  .empty-chat {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #777;
    padding: 3rem;
    text-align: center;
    animation: fadeIn 0.8s ease-out;
  }
  
  .empty-illustration {
    background: rgba(64, 158, 255, 0.1);
    width: 100px;
    height: 100px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 2rem;
  }
  
  .empty-icon {
    font-size: 3rem;
    color: #409eff;
  }
  
  .empty-title {
    font-size: 1.6rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 1rem;
  }
  
  .empty-hint {
    font-size: 1rem;
    color: #888;
    max-width: 280px;
    line-height: 1.5;
  }
  
  /* 消息气泡 */
  .message {
    display: flex;
    align-items: flex-start;
    gap: 0.8rem;
    max-width: 80%;
    transition: all 0.3s ease;
  }
  
  .message-in {
    animation: fadeInMessage 0.5s forwards;
  }
  
  .message.ai {
    align-self: flex-start;
    margin-right: auto;
  }
  
  .message.user {
    align-self: flex-end;
    margin-left: auto;
    flex-direction: row-reverse;
  }
  
  .message-avatar {
    flex-shrink: 0;
    border: 2px solid white;
    box-shadow: 0 3px 8px rgba(0,0,0,0.12);
  }
  
  .message-content {
    position: relative;
    padding: 1rem 1.4rem;
    border-radius: 18px;
    box-shadow: 0 3px 8px rgba(0,0,0,0.08);
    word-break: break-word;
    transition: all 0.2s ease;
  }
  
  .message.ai .message-content {
    background-color: #F4F7F9;
    color: #333;
    border-bottom-left-radius: 4px;
  }
  
  .message.user .message-content {
    background: linear-gradient(135deg, #409eff, #337ecc);
    color: white;
    border-bottom-right-radius: 4px;
  }
  
  .message.ai .message-content::before {
    content: "";
    position: absolute;
    left: -10px;
    top: 15px;
    border-top: 10px solid transparent;
    border-right: 12px solid #F4F7F9;
    border-bottom: 10px solid transparent;
  }
  
  .message.user .message-content::before {
    content: "";
    position: absolute;
    right: -10px;
    top: 15px;
    border-top: 10px solid transparent;
    border-left: 12px solid #337ecc;
    border-bottom: 10px solid transparent;
  }
  
  .message-text {
    font-size: 1rem;
    line-height: 1.6;
    white-space: pre-wrap;
  }
  
  .message-time {
    font-size: 0.75rem;
    opacity: 0.8;
    text-align: right;
    margin-top: 0.5rem;
  }
  
  /* 底部输入区域 */
  .chat-footer {
    padding: 1.2rem;
    background: white;
    border-top: 1px solid rgba(0,0,0,0.05);
    box-shadow: 0 -2px 15px rgba(0,0,0,0.05);
    position: relative;
    z-index: 5;
  }
  
  .controls {
    display: flex;
    gap: 1rem;
    margin-bottom: 1.2rem;
  }
  
  .control-button {
    height: auto;
    padding: 0.7rem 1.3rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 1rem;
    font-weight: 500;
    transition: all 0.3s ease;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  }
  
  .control-button:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 6px 15px rgba(0,0,0,0.15);
  }
  
  .input-container {
    display: flex;
    flex-direction: column;
  }
  
  .input-wrapper {
    display: flex;
    align-items: flex-end;
    gap: 12px;
    background: white;
    border-radius: 20px;
    padding: 12px 16px;
    box-shadow: 0 3px 10px rgba(0,0,0,0.08);
    border: 1px solid rgba(0,0,0,0.08);
    transition: all 0.3s ease;
  }
  
  .input-wrapper:focus-within {
    box-shadow: 0 5px 15px rgba(64, 158, 255, 0.15);
    border-color: rgba(64, 158, 255, 0.3);
  }
  
  .message-input {
    flex: 1;
  }
  
  .message-input :deep(.el-textarea__inner) {
    border: none;
    box-shadow: none;
    padding: 0;
    resize: none;
    background: transparent;
    font-size: 15px;
    min-height: 24px;
    line-height: 24px;
  }
  
  .message-input :deep(.el-textarea__inner:focus) {
    box-shadow: none;
  }
  
  .send-button-wrapper {
    display: flex;
    flex-shrink: 0;
  }
  
  .send-button {
    width: 44px;
    height: 44px;
    transition: all 0.3s;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    padding: 0;
    margin: 0;
  }
  
  .custom-send-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    transform: rotate(45deg) translateX(-1px);
  }
  
  .send-button:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 6px 15px rgba(64, 158, 255, 0.4);
  }
  
  .input-hint {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #888;
    font-size: 0.85rem;
    margin-top: 0.8rem;
    padding-left: 1rem;
  }
  
  /* 打字动画指示器 */
  .typing-indicator {
    display: flex;
    align-items: center;
    padding: 0.8rem 1.2rem;
    background: #F4F7F9;
    border-radius: 18px;
    width: fit-content;
    margin: 0.5rem 0 0 4.5rem;
    position: absolute;
    bottom: 7rem;
    left: 1rem;
    box-shadow: 0 3px 8px rgba(0,0,0,0.08);
    z-index: 2;
    animation: fadeIn 0.3s forwards;
  }
  
  .typing-dot {
    background-color: #999;
    border-radius: 50%;
    width: 8px;
    height: 8px;
    margin: 0 3px;
    animation: typingAnimation 1.2s infinite ease-in-out;
  }
  
  .typing-dot:nth-child(1) {
    animation-delay: 0s;
  }
  
  .typing-dot:nth-child(2) {
    animation-delay: 0.2s;
  }
  
  .typing-dot:nth-child(3) {
    animation-delay: 0.4s;
  }
  
  /* 动画 */
  @keyframes fadeInMessage {
    0% { opacity: 0; transform: translateY(15px); }
    100% { opacity: 1; transform: translateY(0); }
  }
  
  @keyframes fadeIn {
    0% { opacity: 0; }
    100% { opacity: 1; }
  }
  
  @keyframes typingAnimation {
    0%, 60%, 100% { transform: translateY(0); }
    30% { transform: translateY(-5px); }
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .chat-header {
      padding: 0.8rem 1rem;
    }
    
    .room-id {
      font-size: 1.1rem;
    }
    
    .message {
      max-width: 85%;
    }
    
    .message-content {
      padding: 0.8rem 1.1rem;
    }
    
    .message-text {
      font-size: 0.95rem;
    }
    
    .controls {
      flex-wrap: wrap;
    }
    
    .control-button {
      flex: 1;
      min-width: 120px;
      justify-content: center;
      padding: 0.6rem 1rem;
    }
    
    .empty-illustration {
      width: 80px;
      height: 80px;
      margin-bottom: 1.5rem;
    }
    
    .empty-icon {
      font-size: 2.5rem;
    }
    
    .empty-title {
      font-size: 1.4rem;
    }
    
    .empty-hint {
      font-size: 0.9rem;
    }
    
    .typing-indicator {
      bottom: 6.5rem;
    }
  }
</style>