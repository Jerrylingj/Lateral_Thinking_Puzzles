import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ChatView from '../views/ChatView.vue'
import HistoryView from '../views/HistoryView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView
  },
  {
    path: '/chat/:roomId',
    name: 'Chat',
    component: ChatView,
    props: true
  },
  {
    path: '/history',
    name: 'History',
    component: HistoryView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router