<template>
  <div class="history-container">
    <div class="history-header">
      <el-button 
        type="text" 
        class="back-button" 
        @click="$router.push('/')"
      >
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <h1 class="page-title">往期对话记录</h1>
    </div>

    <div class="history-content">
      <div class="history-wrapper">
        <template v-if="loading">
          <div class="loading-container">
            <el-skeleton :rows="6" animated>
              <template #template>
                <div style="padding: 20px">
                  <el-skeleton-item variant="image" style="width: 100%; height: 150px" />
                  <div style="display: flex; align-items: center; margin-top: 20px">
                    <el-skeleton-item variant="circle" style="margin-right: 16px; width: 40px; height: 40px" />
                    <el-skeleton-item variant="p" style="width: 80%" />
                  </div>
                  <div style="margin-top: 16px">
                    <el-skeleton-item variant="text" style="width: 30%; margin-right: 16px" />
                    <el-skeleton-item variant="text" style="width: 40%" />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>
        </template>

        <template v-else-if="history.length === 0">
          <div class="empty-history">
            <el-empty 
              description="暂无历史记录" 
              :image-size="180"
            >
              <p class="empty-description">开始你的推理之旅，解开谜团</p>
              <el-button type="primary" @click="$router.push('/')" size="large" class="start-game-btn">
                <el-icon><CaretRight /></el-icon>
                开始新游戏
              </el-button>
            </el-empty>
          </div>
        </template>

        <template v-else>
          <div class="history-list">
            <div class="history-stats">
              <div class="stat-item">
                <div class="stat-value">{{ history.length }}</div>
                <div class="stat-label">总对话数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ getCompletedGames() }}</div>
                <div class="stat-label">已完成</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ getActiveGames() }}</div>
                <div class="stat-label">进行中</div>
              </div>
            </div>
            
            <transition-group 
              name="fade-slide" 
              tag="div"
              class="history-items"
            >
              <el-collapse 
                v-model="activeKey"
                class="custom-collapse"
              >
                <el-collapse-item 
                  v-for="room in history" 
                  :key="room.roomId"
                  :name="room.roomId.toString()"
                  class="history-item"
                >
                  <template #title>
                    <div class="history-item-header">
                      <el-tag 
                        size="small" 
                        :type="isChatEnded(room) ? 'success' : 'warning'"
                        class="status-tag"
                      >
                        {{ isChatEnded(room) ? '已完成' : '进行中' }}
                      </el-tag>
                      <el-icon class="history-icon"><Document /></el-icon>
                      <span class="header-text">{{ formatRoomHeader(room) }}</span>
                    </div>
                  </template>
                  
                  <div class="room-messages">
                    <div 
                      v-for="(message, index) in room.messages" 
                      :key="index"
                      class="history-message"
                      :class="message.sender"
                    >
                      <el-avatar 
                        :size="36" 
                        :src="message.sender === 'ai' ? AI_AVATAR : USER_AVATAR"
                        class="message-avatar"
                      />
                      <div class="message-content">
                        <div class="message-text">{{ message.content }}</div>
                        <div class="message-time">{{ formatTime(message.timestamp) }}</div>
                      </div>
                    </div>
                  </div>
                  
                  <div class="panel-footer">
                    <el-tooltip 
                      content="继续当前对话" 
                      placement="top" 
                      :disabled="isChatEnded(room)"
                    >
                      <div style="display: inline-block">
                        <el-button 
                          type="primary" 
                          size="small"
                          @click="continueChat(room.roomId)"
                          :disabled="isChatEnded(room)"
                          class="action-button continue-button"
                        >
                          <el-icon><ChatLineRound /></el-icon>
                          继续对话
                        </el-button>
                      </div>
                    </el-tooltip>
                    <el-popconfirm
                      title="确定要删除这条对话记录吗?"
                      confirm-button-text="确定"
                      cancel-button-text="取消"
                      @confirm="deleteHistory(room.roomId)"
                    >
                      <template #reference>
                        <el-button 
                          type="danger" 
                          size="small"
                          class="action-button delete-button"
                        >
                          <el-icon><Delete /></el-icon>
                          删除记录
                        </el-button>
                      </template>
                    </el-popconfirm>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </transition-group>
          </div>
        </template>
      </div>
    </div>
    
    <el-backtop :right="20" :bottom="20" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Document, CaretRight, ChatLineRound, Delete } from '@element-plus/icons-vue'
import axios from 'axios'

const API_BASE = 'http://localhost:3000/api/chat'

const router = useRouter()
const loading = ref(true)
const history = ref([])
const activeKey = ref([])

const AI_AVATAR = '/ai-avatar.svg'
const USER_AVATAR = '/user-avatar.svg'

// 获取已完成游戏数量
const getCompletedGames = () => {
  return history.value.filter(room => {
    if (!room.messages || room.messages.length === 0) return false;
    const lastMessage = room.messages[room.messages.length - 1];
    return lastMessage.content.includes('游戏已结束');
  }).length;
}

// 获取进行中游戏数量
const getActiveGames = () => {
  return history.value.length - getCompletedGames();
}

// 加载历史记录
const loadHistory = async () => {
  try {
    loading.value = true
    const { data } = await axios.get(`${API_BASE}/rooms`)
    
    if (data && Array.isArray(data)) {
      history.value = data.sort((a, b) => {
        // 按最后消息时间倒序排列
        const aTime = a.messages && a.messages.length > 0 
          ? new Date(a.messages[a.messages.length - 1].timestamp).getTime() 
          : 0
        const bTime = b.messages && b.messages.length > 0 
          ? new Date(b.messages[b.messages.length - 1].timestamp).getTime() 
          : 0
        return bTime - aTime
      })
      
      // 默认展开第一条记录
      if (history.value.length > 0) {
        activeKey.value = [history.value[0].roomId.toString()]
      }
    }
  } catch (error) {
    ElMessage.error('获取历史记录失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 格式化房间标题
const formatRoomHeader = (room) => {
  if (!room.messages || room.messages.length === 0) {
    return `房间 ${room.roomId} - 暂无对话`
  }
  
  // 获取房间创建时间
  const firstMsgTime = new Date(room.messages[0].timestamp)
  const formattedDate = firstMsgTime.toLocaleDateString()
  const formattedTime = firstMsgTime.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
  
  // 获取对话摘要
  const firstAIMessage = room.messages.find(msg => msg.sender === 'ai')
  const summary = firstAIMessage 
    ? firstAIMessage.content.substring(0, 50) + (firstAIMessage.content.length > 50 ? '...' : '')
    : '无摘要'
  
  return `${formattedDate} ${formattedTime} - ${summary}`
}

// 格式化消息时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 判断对话是否已结束
const isChatEnded = (room) => {
  if (!room.messages || room.messages.length === 0) return false
  
  const lastMessage = room.messages[room.messages.length - 1]
  return lastMessage.content.includes('游戏已结束')
}

// 继续对话
const continueChat = (roomId) => {
  router.push(`/chat/${roomId}`)
}

// 删除历史记录
const deleteHistory = async (roomId) => {
  try {
    await axios.delete(`${API_BASE}/rooms/${roomId}`)
    ElMessage({
      message: '删除成功',
      type: 'success',
      duration: 2000
    })
    // 重新加载历史记录
    await loadHistory()
  } catch (error) {
    ElMessage.error('删除失败')
    console.error(error)
  }
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.history-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(170deg, #f6f8fc, #eef1f8);
}

.history-header {
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: white;
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  position: relative;
  z-index: 10;
}

.back-button {
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

.back-button:hover {
  background: rgba(255,255,255,0.2);
  transform: scale(1.1);
}

.page-title {
  flex: 1;
  font-size: 1.6rem;
  font-weight: 600;
  text-align: center;
  margin: 0;
  color: white;
  letter-spacing: 1px;
}

.history-content {
  flex: 1;
  padding: 2rem 1rem;
  width: 100%;
  display: flex;
  justify-content: center;
}

.history-wrapper {
  max-width: 900px;
  width: 100%;
}

.loading-container {
  padding: 2rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.05);
}

.empty-history {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 5rem 2rem;
  text-align: center;
  background: white;
  border-radius: 16px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.05);
  animation: fadeIn 0.5s ease-out;
}

.empty-description {
  color: #909399;
  font-size: 1.1rem;
  margin: 1rem 0 2rem;
}

.start-game-btn {
  padding: 0.7rem 2rem;
  font-size: 1.1rem;
  border-radius: 50px;
  transition: all 0.3s ease;
}

.start-game-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 15px rgba(64, 158, 255, 0.2);
}

.history-stats {
  display: flex;
  justify-content: space-around;
  background: white;
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  margin-bottom: 2rem;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #606266;
  font-size: 0.9rem;
}

.history-list {
  overflow: hidden;
}

.history-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.custom-collapse {
  --el-collapse-border-color: transparent;
  --el-collapse-header-height: 60px;
  --el-collapse-header-bg-color: transparent;
}

.history-item {
  border-radius: 16px !important;
  overflow: hidden;
  transition: all 0.3s ease;
  background: white;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  margin-bottom: 1rem;
  border: none !important;
}

.history-item:hover {
  box-shadow: 0 8px 25px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}

.history-item-header {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  font-weight: 500;
}

.header-text {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-icon {
  font-size: 1.1rem;
  color: #409eff;
}

.status-tag {
  margin-right: 5px;
}

.room-messages {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  max-height: 400px;
  overflow-y: auto;
  padding: 1.5rem 1rem;
  margin: 0.5rem 0;
  background: rgba(246, 248, 252, 0.5);
  border-radius: 12px;
  scrollbar-width: thin;
  scrollbar-color: #c0c4cc #f4f4f5;
}

.room-messages::-webkit-scrollbar {
  width: 6px;
}

.room-messages::-webkit-scrollbar-track {
  background: #f4f4f5;
  border-radius: 10px;
}

.room-messages::-webkit-scrollbar-thumb {
  background-color: #c0c4cc;
  border-radius: 10px;
}

.history-message {
  display: flex;
  align-items: flex-start;
  gap: 0.7rem;
  max-width: 80%;
  animation: fadeIn 0.3s ease-out;
}

.history-message.ai {
  align-self: flex-start;
  margin-right: auto;
}

.history-message.user {
  align-self: flex-end;
  margin-left: auto;
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  border: 2px solid white;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.message-content {
  background: #F4F7F9;
  padding: 0.9rem 1.2rem;
  border-radius: 16px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
  word-break: break-word;
  position: relative;
}

.history-message.user .message-content {
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: white;
  border-bottom-right-radius: 4px;
}

.history-message.ai .message-content {
  background: #F4F7F9;
  color: #333;
  border-bottom-left-radius: 4px;
}

.history-message.ai .message-content::before {
  content: "";
  position: absolute;
  left: -8px;
  top: 15px;
  border-top: 8px solid transparent;
  border-right: 10px solid #F4F7F9;
  border-bottom: 8px solid transparent;
}

.history-message.user .message-content::before {
  content: "";
  position: absolute;
  right: -8px;
  top: 15px;
  border-top: 8px solid transparent;
  border-left: 10px solid #337ecc;
  border-bottom: 8px solid transparent;
}

.message-text {
  font-size: 1rem;
  line-height: 1.5;
}

.message-time {
  font-size: 0.75rem;
  opacity: 0.8;
  text-align: right;
  margin-top: 0.5rem;
}

.panel-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.2rem;
  padding-top: 1.2rem;
  border-top: 1px solid #eee;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 5px;
  border-radius: 8px;
  padding: 8px 16px;
  transition: all 0.3s;
}

.continue-button:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(64, 158, 255, 0.3);
}

.delete-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(245, 108, 108, 0.3);
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.5s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

@keyframes fadeIn {
  0% { opacity: 0; transform: translateY(10px); }
  100% { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .history-content {
    padding: 1.2rem 0.8rem;
  }
  
  .page-title {
    font-size: 1.3rem;
  }
  
  .history-stats {
    padding: 1rem;
  }
  
  .stat-value {
    font-size: 1.6rem;
  }
  
  .stat-label {
    font-size: 0.8rem;
  }
  
  .history-message {
    max-width: 90%;
  }
  
  .message-content {
    padding: 0.8rem 1rem;
  }
  
  .message-text {
    font-size: 0.95rem;
  }
  
  .empty-history {
    padding: 3rem 1.5rem;
  }
  
  .action-button {
    padding: 6px 12px;
  }
  
  .empty-description {
    font-size: 1rem;
  }
}
</style> 