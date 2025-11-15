<template>
  <div class="admin-dashboard">
    <div class="container">
      <el-page-header content="管理后台" />

      <div class="stats">
        <el-card class="stat">
          <div class="stat-num">{{ totalProducts }}</div>
          <div class="stat-label">商品总数（本地数据）</div>
        </el-card>
        <el-card class="stat">
          <div class="stat-num">{{ categories.length }}</div>
          <div class="stat-label">分类数量</div>
        </el-card>
      </div>

      <el-card>
        <template #header>
          <div class="card-header">
            <span>最近发布（示例）</span>
          </div>
        </template>
        <el-table :data="latest" style="width: 100%">
          <el-table-column label="#" width="60" type="index" />
          <el-table-column label="图片" width="120">
            <template #default="{ row }">
              <img :src="getImage(row)" class="thumb" alt="thumb" />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="price" label="价格" width="120" />
          <el-table-column prop="location" label="地点" width="160" />
          <el-table-column prop="createdAt" label="发布时间" width="200" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getCategories, queryProducts } from '@/mock/data'

export default {
  name: 'AdminDashboard',
  setup() {
    const categories = ref([])
    const latest = ref([])
    const totalProducts = ref(0)

    const getImage = (row) => {
      try {
        const imgs = JSON.parse(row.images || '[]')
        return imgs[0] || '/placeholder.svg'
      } catch {
        return '/placeholder.svg'
      }
    }

    onMounted(() => {
      categories.value = getCategories()
      const res = queryProducts({ page: 1, size: 10 })
      latest.value = res.records
      totalProducts.value = res.total
    })

    return { categories, latest, totalProducts, getImage }
  }
}
</script>

<style scoped>
.admin-dashboard { min-height: 100vh; background: #f5f5f5; padding: 20px 0; }
.container { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin: 16px 0 20px; }
.stat { text-align: center; }
.stat-num { font-size: 28px; font-weight: 700; color: #1f2d3d; }
.stat-label { color: #606266; margin-top: 4px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.thumb { width: 80px; height: 56px; object-fit: cover; border-radius: 6px; }
</style>
