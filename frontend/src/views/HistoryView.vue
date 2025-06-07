<template>
  <div class="history-view">
    <!-- Header -->
    <header class="history-header">
      <button class="header-btn back-btn" @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <h1 class="page-title">历史归档</h1>
      <div class="header-placeholder"></div>
    </header>

    <!-- Main Content -->
    <main class="history-main">
      <div v-if="loading" class="loading-state">
        <div class="skeleton-card" v-for="n in 3" :key="n"></div>
      </div>

      <div v-else-if="history.length === 0" class="empty-state">
        <div class="empty-icon-wrapper">
          <el-icon><MessageBox /></el-icon>
        </div>
        <h2 class="empty-title">暂无历史记录</h2>
        <p class="empty-subtitle">完成一次推理后，记录将在此处归档</p>
        <button class="empty-action-btn" @click="$router.push('/')">
          <el-icon><CaretRight /></el-icon>
          开启新的推理
        </button>
      </div>

      <div v-else class="history-grid">
        <div v-for="room in history" :key="room.id" class="history-card" @click="continueChat(room.id)">
          <div class="card-header">
            <span class="card-status" :class="room.status.toLowerCase()">
              {{ room.status === 'FINISHED' ? '已完成' : '进行中' }}
            </span>
            <el-popconfirm
                title="确定要永久删除此条记录吗?"
                confirm-button-text="确认删除"
                cancel-button-text="取消"
                :icon="WarningFilled"
                icon-color="#F56C6C"
                @confirm.stop="deleteHistory(room.id)"
                @cancel.stop
              >
              <template #reference>
                <button class="delete-btn" @click.stop>
                  <el-icon><Delete /></el-icon>
                </button>
              </template>
            </el-popconfirm>
          </div>
          <div class="card-content">
            <p class="card-summary">{{ getRoomSummary(room) }}</p>
          </div>
          <div class="card-footer">
            <span class="card-date">{{ formatTimestamp(room.createdAt) }}</span>
            <span class="card-id">ID: {{ room.id }}</span>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, Delete, MessageBox, CaretRight, WarningFilled } from '@element-plus/icons-vue';
import axios from 'axios';

const API_BASE = '/api/chat'; 

const router = useRouter();
const loading = ref(true);
const history = ref([]);

const loadHistory = async () => {
  try {
    loading.value = true;
    const { data } = await axios.get(`${API_BASE}/rooms`);
    if (data && Array.isArray(data)) {
      history.value = data
        .filter(room => room.chatMessageList && room.chatMessageList.length > 0)
        .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
    }
  } catch (error) {
    ElMessage.error('获取历史记录失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const getRoomSummary = (room) => {
  const aiMessage = room.chatMessageList.find(msg => msg.role === 'ASSISTANT');
  if (aiMessage) {
    return aiMessage.content.length > 80 ? aiMessage.content.substring(0, 80) + '...' : aiMessage.content;
  }
  return '暂无摘要';
};

const formatTimestamp = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' });
};

const continueChat = (roomId) => {
  if (history.value.find(r => r.id === roomId && r.status === 'FINISHED')) {
    ElMessage.info('该对话已结束，仅供查阅');
  }
  router.push(`/chat/${roomId}`);
};

const deleteHistory = async (roomId) => {
  try {
    await axios.delete(`${API_BASE}/rooms/${roomId}`);
    ElMessage.success('删除成功');
    loadHistory(); // Refresh the list
  } catch (error) {
    ElMessage.error('删除失败');
    console.error(error);
  }
};

onMounted(loadHistory);
</script>

<style scoped>
/* Base Structure */
.history-view {
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
.history-header {
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
.header-btn:hover { color: #FFFFFF; }
.header-placeholder { width: 40px; } /* To balance the back button */

.page-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #FFFFFF;
}

/* Main Content */
.history-main {
  flex: 1;
  overflow-y: auto;
  padding: 85px 1.5rem 2rem;
  scrollbar-width: thin;
  scrollbar-color: #444 #111;
}
.history-main::-webkit-scrollbar { width: 6px; }
.history-main::-webkit-scrollbar-track { background: #111; }
.history-main::-webkit-scrollbar-thumb { background-color: #444; border-radius: 6px; }

/* Loading State */
.loading-state {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
}
.skeleton-card {
  height: 180px;
  background-color: #1C1C1E;
  border-radius: 12px;
  animation: pulse 1.5s infinite ease-in-out;
}
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: calc(100vh - 150px);
  color: #777;
  text-align: center;
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

.empty-title { font-size: 1.5rem; font-weight: 600; color: #ddd; }
.empty-subtitle { font-size: 1rem; color: #888; margin: 0.5rem 0 2rem; }
.empty-action-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: #0052D4;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}
.empty-action-btn:hover {
  background: #4364F7;
  transform: translateY(-2px);
}

/* History Grid */
.history-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.5rem;
}

.history-card {
  background-color: #1C1C1E;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 180px;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}
.history-card:hover {
  transform: translateY(-5px);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-status {
  font-size: 0.8rem;
  font-weight: 500;
  padding: 0.2rem 0.6rem;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.card-status.finished { background-color: rgba(76, 175, 80, 0.2); color: #4CAF50; }
.card-status.playing { background-color: rgba(255, 152, 0, 0.2); color: #FF9800; }

.delete-btn {
  background: transparent;
  border: none;
  color: #777;
  font-size: 1.1rem;
  cursor: pointer;
  transition: color 0.2s ease;
  padding: 5px;
}
.delete-btn:hover { color: #F56C6C; }

.card-content {
  flex: 1;
  margin: 1rem 0;
  overflow: hidden;
}

.card-summary {
  font-size: 0.95rem;
  color: #AAAAAA;
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: #777;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 0.8rem;
}
.card-id { font-family: monospace; }
</style> 