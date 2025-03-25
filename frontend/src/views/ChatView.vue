<!-- src/views/ChatView.vue -->
<template>
    <a-layout class="container">
      <a-page-header :title="`房间号：${roomId}`" />
      <a-layout-content class="chat-content">
        <a-list :data-source="messages" class="message-list">
          <template #renderItem="{ item }">
            <a-list-item :class="['message-item', item.sender]">
              <a-avatar :src="item.avatar" />
              <div class="bubble" :class="item.sender">
                {{ item.content }}
              </div>
            </a-list-item>
          </template>
        </a-list>
      </a-layout-content>
      <a-layout-footer class="input-area">
        <div class="controls">
          <a-button 
            type="primary" 
            :disabled="!isGameStarted"
            @click="handleGameAction('start')"
          >
            开始
          </a-button>
          <a-button 
            danger 
            :disabled="gameEnded"
            @click="handleGameAction('end')"
          >
            结束
          </a-button>
        </div>
        <a-input-group compact>
          <a-textarea 
            v-model:value="inputMessage"
            placeholder="输入你的猜测..."
            :auto-size="{ minRows: 2, maxRows: 4 }"
            @pressEnter="sendMessage"
          />
          <a-button type="primary" @click="sendMessage">发送</a-button>
        </a-input-group>
      </a-layout-footer>
    </a-layout>
  </template>
  
  <script setup>
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
  .message-item {
    justify-content: flex-start;
    
    &.user {
      flex-direction: row-reverse;
    }
  }
  
  .bubble {
    max-width: 70%;
    padding: 12px;
    border-radius: 8px;
    margin: 0 12px;
    
    &.ai {
      background: #f0f0f0;
    }
    
    &.user {
      background: #1890ff;
      color: white;
    }
  }
  
  .input-area {
    padding: 16px;
    background: white;
    box-shadow: 0 -2px 8px rgba(0,0,0,0.1);
  }
  
  .controls {
    margin-bottom: 8px;
  }
  </style>