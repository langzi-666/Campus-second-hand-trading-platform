<template>
  <div class="product-list">
    <div class="container">
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-input
              v-model="searchParams.keyword"
              placeholder="搜索商品..."
              clearable
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-select
              v-model="searchParams.categoryId"
              placeholder="选择分类"
              clearable
              @change="handleSearch"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="$router.push('/publish')">
              发布商品
            </el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 商品列表 -->
      <div class="product-grid" v-loading="loading">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="handleProductClick(product)"
        >
          <div class="product-image">
            <img :src="getProductImage(product)" :alt="product.title">
            <div class="product-status" v-if="product.status === 2">
              <span class="sold-tag">已售出</span>
            </div>
          </div>
          <div class="product-info">
            <h4 class="product-title">{{ product.title }}</h4>
            <div class="product-price">
              <span class="current-price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">
                ¥{{ product.originalPrice }}
              </span>
            </div>
            <div class="product-meta">
              <span class="condition">{{ getConditionText(product.conditionLevel) }}</span>
              <span class="location">{{ product.location }}</span>
            </div>
            <div class="product-stats">
              <span><el-icon><View /></el-icon> {{ product.viewCount || 0 }}</span>
              <span><el-icon><Star /></el-icon> {{ product.favoriteCount || 0 }}</span>
              <span class="publish-time">{{ formatTime(product.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && products.length === 0" class="empty-state">
        <el-empty description="暂无商品" />
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="searchParams.page"
          v-model:page-size="searchParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategories as mockCategories, queryProducts } from '@/mock/data'

export default {
  name: 'ProductList',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(false)
    const products = ref([])
    const total = ref(0)
    const categories = ref([])
    
    // 搜索参数
    const searchParams = reactive({
      page: 1,
      size: 20,
      keyword: '',
      categoryId: null
    })
    
    // 新旧程度映射
    const conditionMap = {
      1: '全新',
      2: '几乎全新',
      3: '轻微使用',
      4: '明显使用',
      5: '重度使用'
    }
    
    // 加载商品分类
    const loadCategories = async () => {
      try {
        categories.value = mockCategories()
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }
    
    // 加载商品列表
    const loadProducts = async () => {
      try {
        loading.value = true
        const res = queryProducts({
          page: searchParams.page,
          size: searchParams.size,
          keyword: searchParams.keyword,
          categoryId: searchParams.categoryId
        })
        products.value = res.records
        total.value = res.total
      } catch (error) {
        console.error('加载商品失败:', error)
        ElMessage.error('加载商品失败')
      } finally {
        loading.value = false
      }
    }
    
    // 搜索商品
    const handleSearch = () => {
      searchParams.page = 1
      loadProducts()
    }
    
    // 点击商品
    const handleProductClick = (product) => {
      router.push(`/product/${product.id}`)
    }
    
    // 获取商品图片
    const getProductImage = (product) => {
      try {
        const images = JSON.parse(product.images || '[]')
        return images.length > 0 ? images[0] : '/placeholder.svg'
      } catch {
        return '/placeholder.svg'
      }
    }
    
    // 获取新旧程度文本
    const getConditionText = (level) => {
      return conditionMap[level] || '未知'
    }
    
    // 格式化时间
    const formatTime = (timeStr) => {
      const time = new Date(timeStr)
      const now = new Date()
      const diff = now - time
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) { // 1天内
        return Math.floor(diff / 3600000) + '小时前'
      } else if (diff < 2592000000) { // 30天内
        return Math.floor(diff / 86400000) + '天前'
      } else {
        return time.toLocaleDateString()
      }
    }
    
    // 初始化搜索参数
    const initSearchParams = () => {
      if (route.query.keyword) {
        searchParams.keyword = route.query.keyword
      }
      if (route.query.categoryId) {
        searchParams.categoryId = parseInt(route.query.categoryId)
      }
    }
    
    onMounted(() => {
      initSearchParams()
      loadCategories()
      loadProducts()
    })
    
    return {
      loading,
      products,
      total,
      categories,
      searchParams,
      handleSearch,
      handleProductClick,
      getProductImage,
      getConditionText,
      formatTime
    }
  }
}
</script>

<style scoped>
.product-list {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-filter {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-status {
  position: absolute;
  top: 10px;
  right: 10px;
}

.sold-tag {
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 10px 0;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  margin-bottom: 8px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
  color: #666;
}

.product-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.product-stats span {
  display: flex;
  align-items: center;
  gap: 2px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 15px;
  }
  
  .search-filter {
    padding: 15px;
  }
}
</style>
