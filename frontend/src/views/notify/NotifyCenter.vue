<template>
  <div class="notify-center">
    <div class="container">
      <div class="header-row">
        <h2>系统通知</h2>
        <div class="actions">
          <el-button type="primary" size="small" @click="handleMarkAllRead" :disabled="unreadCount===0">
            全部设为已读<span v-if="unreadCount">（{{ unreadCount }}）</span>
          </el-button>
        </div>
      </div>

      <el-empty v-if="list.length===0" description="暂无通知" class="mt-20" />

      <el-timeline v-else>
        <el-timeline-item
          v-for="item in list"
          :key="item.id"
          :timestamp="formatTime(item.createdAt)"
          :type="item.read ? 'info' : 'primary'"
        >
          <el-card :class="['notify-card', { unread: !item.read }]">
            <div class="title">{{ item.title }}</div>
            <div class="content">{{ item.content }}</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'NotifyCenter',
  setup() {
    const store = useStore()
    const list = computed(() => store.getters['notify/list'])
    const unreadCount = computed(() => store.getters['notify/unreadCount'])

    const handleMarkAllRead = () => {
      store.dispatch('notify/markAllRead')
    }

    const formatTime = (ts) => new Date(ts).toLocaleString()

    return { list, unreadCount, handleMarkAllRead, formatTime }
  }
}
</script>

<style scoped>
.notify-center { min-height: 100vh; background: #f5f5f5; padding: 20px 0; }
.container { max-width: 900px; margin: 0 auto; padding: 0 20px; }
.header-row { display: flex; align-items: center; justify-content: space-between; }
.mt-20 { margin-top: 20px; }
.notify-card.unread { border-left: 4px solid var(--el-color-primary); }
.notify-card .title { font-weight: 600; color: #1f2d3d; margin-bottom: 6px; }
.notify-card .content { color: #606266; }
</style>
