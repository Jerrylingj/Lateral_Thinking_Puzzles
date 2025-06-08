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
        <div class="skeleton-card" v-for="n in 5" :key="n" :style="{ animationDelay: `${n * 0.05}s` }"></div>
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
        <div 
          v-for="(room, index) in history" 
          :key="room.id" 
          class="history-card"
          :class="{ 'is-mine': room.userId === myUserId }"
          @click="continueChat(room.id)"
          :style="{ animationDelay: `${index * 0.05}s` }"
        >
          <div class="card-header">
            <span class="card-status" :class="room.status.toLowerCase()">
              {{ room.status === 'FINISHED' ? '已完成' : '进行中' }}
            </span>
            <el-popconfirm
                v-if="room.userId === myUserId"
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
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, Delete, MessageBox, CaretRight, WarningFilled } from '@element-plus/icons-vue';
import { getUserId } from '@/utils/user';

const API_BASE = '/api/chat'; 

const { proxy } = getCurrentInstance();
const router = useRouter();
const loading = ref(true);
const history = ref([]);
const myUserId = ref('');

onMounted(async () => {
  myUserId.value = getUserId();
  await loadHistory();
});

const loadHistory = async () => {
  try {
    loading.value = true;
    const data = await proxy.$api.get(`${API_BASE}/rooms`);
    
    // Log the raw data from the backend to the browser's console for debugging
    console.log('Received history data from backend:', JSON.stringify(data, null, 2));

    if (data && Array.isArray(data)) {
      history.value = data
        .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
    } else {
      // Handle cases where data is not in the expected format
      console.error('Received unexpected data format from /api/chat/rooms:', data);
      history.value = [];
    }
  } catch (error) {
    ElMessage.error('获取历史记录失败');
    console.error('Error fetching history:', error); // Log the full error object
  } finally {
    loading.value = false;
  }
};

const getRoomSummary = (room) => {
  // Defensive check to prevent rendering errors if data structure is unexpected
  if (!room || !Array.isArray(room.chatMessageList) || room.chatMessageList.length === 0) {
    return '暂无摘要';
  }
  const aiMessage = room.chatMessageList.find(msg => msg.role === 'ASSISTANT');
  if (aiMessage && aiMessage.content) {
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
  const room = history.value.find(r => r.id === roomId);
  if (room && room.status === 'FINISHED') {
    ElMessage.info('该对话已结束，仅供查阅');
  }
  router.push(`/chat/${roomId}`);
};

const deleteHistory = async (roomId) => {
  try {
    await proxy.$api.delete(`${API_BASE}/rooms/${roomId}`, {
      params: { userId: myUserId.value }
    });
    ElMessage.success('删除成功');
    await loadHistory();
  } catch (error) {
    const errorMsg = error.response?.data?.message || '删除失败';
    ElMessage.error(errorMsg);
  }
};
</script>

<style scoped>
/* Base Structure */
.history-view {
  height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: column;
  background-color: #F9F9FB;
  color: #1a1a1a;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
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
  padding: 0 1rem;
  z-index: 100;
  height: 65px;
  background-color: rgba(255, 255, 255, 0.6);
  backdrop-filter: saturate(180%) blur(15px);
  -webkit-backdrop-filter: saturate(180%) blur(15px);
  border-bottom: 1px solid #EAEAEA;
}
.header-btn {
  background: transparent; border: none; color: #555; font-size: 1.5rem;
  cursor: pointer; transition: color 0.2s ease; display: flex;
  align-items: center; justify-content: center; width: 44px; height: 44px; border-radius: 50%;
}
.header-btn:hover { color: #000; background-color: rgba(0,0,0,0.05); }
.header-placeholder { width: 44px; }
.page-title { font-size: 1.1rem; font-weight: 600; letter-spacing: -0.5px; }

/* Main Content */
.history-main {
  flex: 1;
  overflow-y: auto;
  padding: 85px 2rem 2rem;
}

/* Loading State */
.loading-state {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.8rem;
}
.skeleton-card {
  height: 190px;
  background: linear-gradient(110deg, #f9f9f9 8%, #f0f0f0 18%, #f9f9f9 33%);
  background-size: 200% 100%;
  border-radius: 20px;
  animation: 1.5s skeleton-shine infinite linear;
}
@keyframes skeleton-shine {
  to {
    background-position-x: -200%;
  }
}

/* Empty State */
.empty-state {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  height: calc(100vh - 150px); color: #A0A0A0; text-align: center;
  animation: fade-in 1s ease;
}
.empty-icon-wrapper {
  font-size: 3rem; width: 90px; height: 90px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #F0F2F5, #E8EAEF);
  margin-bottom: 2rem; color: #B0B0B0;
  box-shadow: 0 4px 15px rgba(0,0,0,0.03);
}
.empty-title { font-size: 1.5rem; font-weight: 600; color: #333; }
.empty-subtitle { font-size: 1rem; color: #888; margin: 0.5rem 0 2.5rem; }
.empty-action-btn {
  display: inline-flex; align-items: center; gap: 0.6rem;
  background: linear-gradient(135deg, #007BFF, #0056B3);
  color: white; border: none; border-radius: 14px;
  padding: 0.9rem 1.8rem; font-size: 1rem; font-weight: 500; cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 122, 255, 0.2);
}
.empty-action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 7px 20px rgba(0, 122, 255, 0.3);
}

/* History Grid */
.history-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1.8rem;
}
.history-card {
  background-color: #FFFFFF;
  border: 1px solid #EBEBEB;
  border-radius: 20px;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  height: 190px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}
.history-card:hover {
  transform: translateY(-6px);
  border-color: #DCDCDC;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

/* Card for "My Rooms" */
.history-card.is-mine::before {
  content: '';
  position: absolute;
  top: -80px;
  right: -80px;
  width: 160px;
  height: 160px;
  background: radial-gradient(circle, rgba(0, 122, 255, 0.1) 0%, rgba(0, 122, 255, 0) 60%);
  transition: transform 0.4s ease;
  transform: scale(0);
}
.history-card.is-mine:hover::before {
  transform: scale(1);
}

.card-header { display: flex; justify-content: space-between; align-items: flex-start; z-index: 1; }
.card-status {
  font-size: 0.8rem; font-weight: 500; padding: 0.3rem 0.8rem;
  border-radius: 8px; text-transform: capitalize; letter-spacing: 0.3px;
}
.card-status.finished { background-color: rgba(52, 199, 89, 0.1); color: #28a745; border: 1px solid rgba(52, 199, 89, 0.2); }
.card-status.playing { background-color: rgba(255, 149, 0, 0.1); color: #fd7e14; border: 1px solid rgba(255, 149, 0, 0.2); }

.delete-btn {
  background: transparent; border: none; color: #C0C0C0;
  font-size: 1.2rem; cursor: pointer; transition: all 0.2s ease;
  padding: 6px; border-radius: 50%;
}
.delete-btn:hover { color: #FF453A; background-color: rgba(255, 69, 58, 0.1); }

.card-content { flex: 1; margin: 1.2rem 0; overflow: hidden; }
.card-summary {
  font-size: 0.95rem; color: #555; line-height: 1.65; margin: 0;
  display: -webkit-box; -webkit-line-clamp: 2;
  -webkit-box-orient: vertical; overflow: hidden; text-overflow: ellipsis;
}

.card-footer {
  display: flex; justify-content: space-between; align-items: center;
  font-size: 0.8rem; color: #AAAAAA;
  border-top: 1px solid #F5F5F5;
  padding-top: 1rem;
}
.card-id { font-family: monospace; letter-spacing: -0.5px; }

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fade-in-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style> 