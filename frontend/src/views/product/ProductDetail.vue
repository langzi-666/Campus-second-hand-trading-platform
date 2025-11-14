<template>
  <div class="product-detail">
    <div class="container" v-loading="loading">
      <div v-if="product" class="detail-content">
        <el-row :gutter="30">
          <!-- 商品图片 -->
          <el-col :span="12">
            <div class="product-images">
              <el-carousel v-if="productImages.length > 0" height="400px">
                <el-carousel-item v-for="(image, index) in productImages" :key="index">
                  <img :src="image" :alt="product.title" class="carousel-image">
                </el-carousel-item>
              </el-carousel>
              <div v-else class="no-image">
                <img src="/placeholder.svg" alt="暂无图片" class="placeholder-image">
              </div>
            </div>
          </el-col>
          
          <!-- 商品信息 -->
          <el-col :span="12">
            <div class="product-info">
              <h1 class="product-title">{{ product.title }}</h1>
              
              <div class="product-price">
                <span class="current-price">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="original-price">
                  原价：¥{{ product.originalPrice }}
                </span>
              </div>
              
              <div class="product-meta">
                <div class="meta-item">
                  <span class="label">新旧程度：</span>
                  <span class="value">{{ getConditionText(product.conditionLevel) }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">交易地点：</span>
                  <span class="value">{{ product.location || '面议' }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">发布时间：</span>
                  <span class="value">{{ formatTime(product.createdAt) }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">浏览次数：</span>
                  <span class="value">{{ product.viewCount || 0 }}</span>
                </div>
              </div>
              
              <div class="product-actions" v-if="!isOwner">
                <el-button type="primary" size="large" @click="handleContact">
                  <el-icon><Message /></el-icon>
                  联系卖家
                </el-button>
                <el-button size="large" @click="handleFavorite">
                  <el-icon><Star /></el-icon>
                  收藏
                </el-button>
              </div>
              
              <div class="owner-actions" v-else>
                <el-button type="primary" @click="handleEdit">编辑商品</el-button>
                <el-button v-if="product.status === 1" type="warning" @click="handleStatusChange(3)">
                  下架商品
                </el-button>
                <el-button v-if="product.status === 3" type="success" @click="handleStatusChange(1)">
                  重新上架
                </el-button>
                <el-button v-if="product.status === 1" type="info" @click="handleStatusChange(2)">
                  标记已售
                </el-button>
                <el-button type="danger" @click="handleDelete">删除商品</el-button>
              </div>
              
              <div v-if="product.contactInfo" class="contact-info">
                <h3>联系方式</h3>
                <p>{{ product.contactInfo }}</p>
              </div>
            </div>
          </el-col>
        </el-row>
        
        <!-- 商品描述 -->
        <div class="product-description">
          <h3>商品描述</h3>
          <div class="description-content">
            {{ product.description || '暂无描述' }}
          </div>
        </div>
        
        <!-- 卖家信息 -->
        <div class="seller-info" v-if="seller">
          <h3>卖家信息</h3>
          <div class="seller-card">
            <el-avatar :src="seller.avatar" :size="60">
              {{ seller.nickname?.charAt(0) }}
            </el-avatar>
            <div class="seller-details">
              <h4>{{ seller.nickname }}</h4>
              <p>{{ seller.school }} {{ seller.college }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 商品不存在 -->
      <div v-else-if="!loading" class="not-found">
        <el-result
          icon="warning"
          title="商品不存在"
          sub-title="该商品可能已被删除或不存在"
        >
          <template #extra>
            <el-button type="primary" @click="$router.push('/products')">
              返回商品列表
            </el-button>
          </template>
        </el-result>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

export default {
  name: 'ProductDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const store = useStore()
    const loading = ref(false)
    const product = ref(null)
    const seller = ref(null)
    
    // 计算属性
    const currentUser = computed(() => store.getters['auth/currentUser'])
    const isOwner = computed(() => {
      return product.value && currentUser.value && 
             product.value.userId === currentUser.value.id
    })
    
    const productImages = computed(() => {
      if (!product.value?.images) return []
      try {
        return JSON.parse(product.value.images)
      } catch {
        return []
      }
    })
    
    // 新旧程度映射
    const conditionMap = {
      1: '全新',
      2: '几乎全新', 
      3: '轻微使用',
      4: '明显使用',
      5: '重度使用'
    }
    
    // 加载商品详情
    const loadProduct = async () => {
      try {
        loading.value = true
        const productId = route.params.id
        
        // 暂时使用模拟数据
        const mockProduct = {
          id: parseInt(productId),
          userId: 2,
          title: 'iPhone 13 128G 蓝色',
          price: 4500,
          originalPrice: 5999,
          conditionLevel: 2,
          location: '宿舍楼下',
          contactInfo: '微信：abc123456',
          description: '9成新iPhone 13，蓝色128G版本。购买时间2022年3月，平时使用爱护，功能完全正常，外观几乎无磨损。配件齐全：原装充电器、数据线、耳机转接头、包装盒、说明书等。因为换了新手机所以出售，诚心出售，价格可小刀。',
          viewCount: 156,
          favoriteCount: 23,
          status: 1,
          images: '[]',
          createdAt: '2024-11-14T10:00:00'
        }
        
        const mockSeller = {
          id: 2,
          nickname: '张三',
          avatar: '',
          school: '示例大学',
          college: '计算机学院'
        }
        
        product.value = mockProduct
        seller.value = mockSeller
        
      } catch (error) {
        console.error('加载商品详情失败:', error)
        ElMessage.error('加载商品详情失败')
      } finally {
        loading.value = false
      }
    }
    
    // 获取新旧程度文本
    const getConditionText = (level) => {
      return conditionMap[level] || '未知'
    }
    
    // 格式化时间
    const formatTime = (timeStr) => {
      return new Date(timeStr).toLocaleString()
    }
    
    // 联系卖家
    const handleContact = () => {
      if (!currentUser.value) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }
      
      // 跳转到聊天页面
      router.push(`/messages?userId=${product.value.userId}`)
    }
    
    // 收藏商品
    const handleFavorite = async () => {
      if (!currentUser.value) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }
      
      try {
        // 调用收藏API
        ElMessage.success('收藏成功')
      } catch (error) {
        ElMessage.error('收藏失败')
      }
    }
    
    // 编辑商品
    const handleEdit = () => {
      router.push(`/product/${product.value.id}/edit`)
    }
    
    // 更改商品状态
    const handleStatusChange = async (status) => {
      try {
        const statusText = {
          1: '上架',
          2: '标记已售',
          3: '下架'
        }
        
        await ElMessageBox.confirm(
          `确定要${statusText[status]}这个商品吗？`,
          '确认操作',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 调用状态更新API
        product.value.status = status
        ElMessage.success(`${statusText[status]}成功`)
        
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('操作失败')
        }
      }
    }
    
    // 删除商品
    const handleDelete = async () => {
      try {
        await ElMessageBox.confirm(
          '确定要删除这个商品吗？删除后无法恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 调用删除API
        ElMessage.success('删除成功')
        router.push('/user')
        
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    onMounted(() => {
      loadProduct()
    })
    
    return {
      loading,
      product,
      seller,
      isOwner,
      productImages,
      getConditionText,
      formatTime,
      handleContact,
      handleFavorite,
      handleEdit,
      handleStatusChange,
      handleDelete
    }
  }
}
</script>

<style scoped>
.product-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.detail-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.product-images {
  border-radius: 8px;
  overflow: hidden;
}

.carousel-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.no-image {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
}

.placeholder-image {
  width: 200px;
  height: 200px;
  object-fit: cover;
  opacity: 0.5;
}

.product-info {
  padding-left: 20px;
}

.product-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.product-price {
  margin-bottom: 20px;
}

.current-price {
  font-size: 28px;
  font-weight: bold;
  color: #e74c3c;
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
  margin-left: 15px;
}

.product-meta {
  margin-bottom: 30px;
}

.meta-item {
  display: flex;
  margin-bottom: 10px;
}

.label {
  width: 80px;
  color: #666;
  font-weight: 500;
}

.value {
  color: #333;
}

.product-actions,
.owner-actions {
  margin-bottom: 30px;
}

.product-actions .el-button,
.owner-actions .el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}

.contact-info {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-top: 20px;
}

.contact-info h3 {
  margin-bottom: 10px;
  color: #333;
}

.product-description {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.product-description h3 {
  margin-bottom: 15px;
  color: #333;
}

.description-content {
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
}

.seller-info {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.seller-info h3 {
  margin-bottom: 15px;
  color: #333;
}

.seller-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.seller-details {
  margin-left: 15px;
}

.seller-details h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.seller-details p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.not-found {
  background: white;
  border-radius: 8px;
  padding: 60px 30px;
  text-align: center;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .detail-content {
    padding: 20px 15px;
  }
  
  .product-info {
    padding-left: 0;
    margin-top: 20px;
  }
  
  .product-title {
    font-size: 20px;
  }
  
  .current-price {
    font-size: 24px;
  }
}
</style>
