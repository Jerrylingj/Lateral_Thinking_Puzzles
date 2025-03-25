<!-- src/views/ChatView.vue -->
<template>
  <div class="chat-container">
    <!-- 顶部导航栏 -->
    <div class="chat-header">
      <a-button 
        type="text" 
        class="back-button" 
        @click="$router.go(-1)"
      >
      <LeftOutlined style="color: black;"/>
      </a-button>
      <h1 class="room-id">房间号：{{ roomId }}</h1>
    </div>

    <!-- 消息主体区域 -->
    <div class="chat-main">
      <div class="message-scroll-container">
        <div class="message-container">
          <div 
            v-for="(message, index) in messages" 
            :key="index" 
            class="message"
            :class="message.sender"
          >
            <a-avatar 
              :src="message.avatar" 
              size="small" 
              class="message-avatar"
              loading="lazy"
            />
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部控制区域 -->
    <div class="chat-footer">
      <div class="controls">
        <a-button 
          type="primary" 
          :disabled="!isGameStarted"
          @click="handleGameAction('start')"
          class="control-button"
        >
          <template #icon><PlayCircleOutlined /></template>
          开始游戏
        </a-button>
        <a-button 
          danger 
          :disabled="gameEnded"
          @click="handleGameAction('end')"
          class="control-button"
        >
          <template #icon><PoweroffOutlined /></template>
          结束游戏
        </a-button>
      </div>

      <div class="input-group">
        <a-textarea
          v-model:value="inputMessage"
          placeholder="输入你的猜测..."
          :auto-size="{ minRows: 1, maxRows: 4 }"
          class="message-input"
          @pressEnter="sendMessage"
        />
        <a-button 
          type="primary" 
          @click="sendMessage" 
          class="send-button"
        >
          <template #icon><SendOutlined /></template>
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { PlayCircleOutlined, PoweroffOutlined, SendOutlined, LeftOutlined } from '@ant-design/icons-vue'
  import { ref, reactive, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import { message } from 'ant-design-vue'
  import axios from 'axios'

  const API_BASE = 'http://localhost:3000/api/chat' // 修正后端端口

  // 强制转换roomId为数值类型
  const route = useRoute()
  const roomId = Number(route.params.roomId) // 转换为Number类型

const inputMessage = ref('')
const isGameStarted = ref(false)
const gameEnded = ref(false)

const messages = reactive([])

const AI_AVATAR = '/ai-avatar.png'
const USER_AVATAR = '/user-avatar.png'

const handleGameAction = async (action) => {
  try {
    const response = await axios.post(`http://localhost:3000/api/chat/${roomId}/send?message=${action}`)
    addMessage('ai', response.data)
    isGameStarted.value = true
  } catch (error) {
    message.error('操作失败')
  }
}

const addMessage = (sender, content) => {
  messages.push({
    sender,
    content,
    avatar: sender === 'ai' ? AI_AVATAR : USER_AVATAR
  })
}

const checkGameEnd = (content) => {
  if (content.includes('游戏已结束')) {
    gameEnded.value = true
  }
}

  // 消息发送逻辑调整
  const sendMessage = async () => {
    if (!inputMessage.value.trim()) return
    
    try {
      addMessage('user', inputMessage.value)
      
      // 使用URLSearchParams确保参数正确编码
      const params = new URLSearchParams()
      params.append('message', inputMessage.value)
  
      // 使用完整URL构造方式
      const response = await axios.post(
        `${API_BASE}/${roomId}/send?${params.toString()}`,
        null, // 请求体置空
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        }
      )
      
      addMessage('ai', response.data)
      checkGameEnd(response.data)
      inputMessage.value = ''
    } catch (error) {
      handleApiError(error)
    }
  }
  
  // 增强错误处理
  const handleApiError = (error) => {
    if (error.response) {
      const { status, data } = error.response
      message.error(`请求错误 (${status}): ${data}`)
    } else {
      message.error('网络连接异常')
    }
  }
  
  // 历史记录获取调整
  onMounted(async () => {
    try {
      const { data } = await axios.get(`${API_BASE}/rooms`, {
        params: {
          roomId // 显式传递数值型roomId
        }
      })
      // 处理历史记录初始化...
      console.log(data)
    } catch (error) {
      handleApiError(error)
    }
  })

</script>

<style scoped>
  :root {
    --primary-color: #6B8DD6; /* 主色调 */
    --secondary-color: #8E37D7; /* 辅助色 */
    --user-bubble: #4DABFF; /* 用户气泡颜色 */
    --ai-bubble: #FFFFFF; /* AI气泡颜色 */
    --shadow-light: 0 4px 12px rgba(0,0,0,0.05); /* 轻阴影 */
  }
  
  .chat-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
    background: #F6F8FA; /* 消息区域背景色 */
    font-family: 'Segoe UI', system-ui, sans-serif; /* 现代字体 */
  }
  
  /* 顶部导航栏 */
  .chat-header {
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)); /* 渐变背景 */
    color: black;
    box-shadow: var(--shadow-light);
    display: flex;
    align-items: center;
    gap: 16px;
  }
  
  .back-button {
    color: rgba(255,255,255,0.9);
    transition: transform 0.2s ease;
  }
  .back-button:hover {
    color: white;
    transform: translateX(-2px);
  }
  
  .room-id {
    font-size: 1.5rem;
    font-weight: 500;
    letter-spacing: 0.5px;
    margin: 0;
    text-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }
  
  /* 消息主体区域 */
  .chat-main {
    flex: 1;
    overflow: hidden;
    padding: 24px 0;
  }
  
  .message-scroll-container {
    height: 100%;
    overflow-y: auto;
    padding: 0 24px;
  }
  .message-scroll-container::-webkit-scrollbar {
    width: 6px;
  }
  .message-scroll-container::-webkit-scrollbar-thumb {
    background: rgba(0,0,0,0.1);
    border-radius: 4px;
  }
  
  .message-container {
    max-width: 80vw;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  /* 消息气泡 */
  .message {
    --bubble-tail: 8px; /* 气泡尾巴大小 */
    position: relative;
    display: flex;
    align-items: start;
    gap: 12px;
    padding: 16px;
    border-radius: 18px;
    max-width: 70%;
    box-shadow: var(--shadow-light);
    animation: messageIn 0.4s cubic-bezier(0.18, 0.89, 0.32, 1.28); /* 进入动画 */
  }
  
  .message.ai {
    background: white;
    margin-right: auto;
  }
  
  .message.user {
    background: lightskyblue;
    color: white;
    margin-left: auto;
    flex-direction: row-reverse;
  }
  
  .message-avatar {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    box-shadow: var(--shadow-light);
  }
  
  .message-content {
    flex: 1;
    min-width: 0;
  }
  
  .message-text {
    font-size: 1.5rem;
    line-height: 1.5;
    word-break: break-word;
  }
  
  /* 底部输入区域 */
  .chat-footer {
    padding: 16px 24px;
    background: white;
    border-top: 1px solid rgba(0,0,0,0.05);
    box-shadow: 0 -4px 12px rgba(0,0,0,0.03);
  }
  
  .controls {
    margin-bottom: 16px;
    display: flex;
    gap: 12px;
  }
  
  .control-button {
    border-radius: 8px;
    padding: 8px 16px;
    height: auto;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    transition: all 0.2s ease;
  }
  .control-button:hover {
    transform: translateY(-1px);
    box-shadow: var(--shadow-light);
  }
  
  .input-group {
    display: flex;
    gap: 12px;
    align-items: center;
    background: white;
    border-radius: 12px;
    padding: 8px;
    box-shadow: var(--shadow-light);
  }
  
  .message-input {
    border: none;
    background: #F8FAFC;
    border-radius: 8px;
    padding: 12px 16px !important;
    font-size: 1.5rem;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  .message-input:focus {
    box-shadow: 0 0 0 3px rgba(107, 141, 214, 0.2); /* 主色调的焦点效果 */
  }
  
  .send-button {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
  }
  .send-button:hover {
    transform: scale(1.05);
  }
  
  /* 动画 */
  @keyframes messageIn {
    0% { opacity: 0; transform: translateY(20px) scale(0.95); }
    100% { opacity: 1; transform: translateY(0) scale(1); }
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .message-container {
      padding: 0 12px;
    }
    .message {
      max-width: 85%;
      padding: 12px;
    }
    .message::before, .message::after {
      display: none; /* 移动端隐藏气泡尾巴 */
    }
    .message-avatar {
      width: 32px;
      height: 32px;
    }
    .chat-footer {
      padding: 12px;
    }
    .control-button {
      font-size: 1.5rem;
      padding: 6px 12px;
    }
  }
  </style>