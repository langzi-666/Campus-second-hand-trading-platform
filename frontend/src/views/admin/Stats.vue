<template>
  <div class="admin-stats">
    <div class="container">
      <el-page-header content="数据统计" />

      <el-card class="mt-20">
        <div class="summary">
          <div class="summary-item">
            <div class="num">{{ stats.totalProducts }}</div>
            <div class="label">商品总数</div>
          </div>
          <div class="summary-item" v-for="c in stats.categories" :key="c.id">
            <div class="label">{{ c.name }}</div>
            <el-progress :percentage="percent(c.count)" :text-inside="true" :stroke-width="18" status="success" />
            <div class="count">{{ c.count }}</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getStats } from '@/mock/data'

export default {
  name: 'AdminStats',
  setup() {
    const stats = ref({ totalProducts: 0, categories: [] })

    onMounted(() => {
      stats.value = getStats()
    })

    const percent = (count) => {
      if (!stats.value.totalProducts) return 0
      return Math.round((count * 100) / stats.value.totalProducts)
    }

    return { stats, percent }
  }
}
</script>

<style scoped>
.admin-stats { min-height: 100vh; background: #f5f5f5; padding: 20px 0; }
.container { max-width: 1000px; margin: 0 auto; padding: 0 20px; }
.mt-20 { margin-top: 20px; }
.summary { display: grid; grid-template-columns: 1fr; gap: 16px; }
.summary-item { background: #fff; padding: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.num { font-size: 28px; font-weight: 700; color: #1f2d3d; }
.label { color: #606266; margin-bottom: 8px; }
.count { color: #909399; font-size: 12px; margin-top: 6px; }
</style>
