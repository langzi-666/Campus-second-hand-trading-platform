<template>
  <div class="home">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="container">
        <div class="nav-left">
          <h1 class="logo">校园二手交易平台</h1>
        </div>
        <div class="nav-right">
          <template v-if="isAuthenticated">
            <el-button type="text" @click="$router.push('/publish')">
              <el-icon><Plus /></el-icon>
              发布商品
            </el-button>
            <el-button v-if="isAdmin" type="text" @click="$router.push('/admin')">
              <el-icon><Setting /></el-icon>
              管理后台
            </el-button>
            <el-button type="text" @click="$router.push('/messages')">
              <el-icon><Message /></el-icon>
              消息
            </el-button>
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-avatar :src="currentUser.avatar" :size="32">
                  {{ currentUser.nickname?.charAt(0) }}
                </el-avatar>
                <span class="username">{{ currentUser.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <el-main class="main">
      <div class="container">
        <div class="hero">
          <div class="hero-content">
            <h2 class="headline">发现校园好物，放心买卖</h2>
            <p class="subtext">二手交易 · 安全便捷 · 价格透明</p>
            <div class="search-wrap">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索商品..."
                size="large"
                class="search-input xl"
                @keyup.enter="handleSearch"
              >
                <template #append>
                  <el-button @click="handleSearch">
                    <el-icon><Search /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
          </div>
        </div>

        <!-- 分类导航 -->
        <div class="content-grid">
        <div class="category-section section-card">
          <h3>商品分类</h3>
          <div class="category-list">
            <div
              v-for="category in categories"
              :key="category.id"
              class="category-item"
              @click="handleCategoryClick(category)"
            >
              <el-icon><Box /></el-icon>
              <span>{{ category.name }}</span>
            </div>
          </div>
        </div>

        <!-- 推荐商品 -->
        <div class="product-section section-card">
          <h3>推荐商品</h3>
          <div class="product-grid">
            <div
              v-for="product in products"
              :key="product.id"
              class="product-card"
              @click="handleProductClick(product)"
            >
              <div class="product-image">
                <img :src="getProductImage(product)" :alt="product.title">
              </div>
              <div class="product-info">
                <h4 class="product-title">{{ product.title }}</h4>
                <p class="product-price">¥{{ product.price }}</p>
                <p class="product-location">{{ product.location }}</p>
              </div>
            </div>
          </div>
        </div>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFeaturedProducts } from '@/mock/data'

export default {
  name: 'Home',
  setup() {
    const store = useStore()
    const router = useRouter()
    const searchKeyword = ref('')
    const categories = ref([
      { id: 1, name: '数码产品' },
      { id: 2, name: '学习用品' },
      { id: 3, name: '生活用品' },
      { id: 4, name: '服装鞋帽' },
      { id: 5, name: '运动器材' },
      { id: 6, name: '其他' }
    ])
    const products = ref([])

    const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
    const currentUser = computed(() => store.getters['auth/currentUser'])
    const isAdmin = computed(() => currentUser.value && currentUser.value.role === 'admin')

    // 处理搜索
    const handleSearch = () => {
      if (searchKeyword.value.trim()) {
        router.push({
          path: '/products',
          query: { keyword: searchKeyword.value }
        })
      }
    }

    // 处理分类点击
    const handleCategoryClick = (category) => {
      router.push({
        path: '/products',
        query: { categoryId: category.id }
      })
    }

    // 处理商品点击
    const handleProductClick = (product) => {
      router.push(`/product/${product.id}`)
    }

    const getProductImage = (product) => {
      try {
        const images = JSON.parse(product.images || '[]')
        return images.length > 0 ? images[0] : '/placeholder.svg'
      } catch {
        return '/placeholder.svg'
      }
    }

    // 处理用户下拉菜单
    const handleCommand = (command) => {
      switch (command) {
        case 'profile':
          router.push('/user')
          break
        case 'logout':
          store.dispatch('auth/logout')
          ElMessage.success('已退出登录')
          break
      }
    }

    const loadProducts = async () => {
      try {
        products.value = getFeaturedProducts(12)
      } catch (error) {
        console.error('加载商品失败:', error)
      }
    }

    onMounted(() => {
      loadProducts()
    })

    return {
      searchKeyword,
      categories,
      products,
      isAuthenticated,
      currentUser,
      isAdmin,
      handleSearch,
      handleCategoryClick,
      handleProductClick,
      handleCommand,
      getProductImage
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.logo {
  color: #409eff;
  font-size: 20px;
  margin: 0;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

.main {
  padding: 20px 0;
}

.main .container {
  display: block;
}

.hero {
  background: linear-gradient(135deg, #eef5ff 0%, #f9fbff 100%);
  border-radius: 12px;
  padding: 32px 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.hero-content { max-width: 880px; margin: 0 auto; text-align: center; }
.headline { font-size: 26px; color: #1f2d3d; margin: 0 0 6px 0; }
.subtext { color: #606266; margin: 0 0 14px 0; }
.search-wrap { display: flex; justify-content: center; }
.search-input.xl { width: 720px; max-width: 100%; }

.content-grid {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  align-items: start;
}

.section-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.search-section {
  text-align: center;
  margin-bottom: 40px;
}

.search-input {
  max-width: 600px;
}

.category-section {
  margin-bottom: 40px;
}

.category-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.category-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.category-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.category-item .el-icon {
  font-size: 24px;
  color: #409eff;
  margin-bottom: 8px;
}

.product-section h3 {
  margin-bottom: 20px;
  color: #333;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
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
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 8px 0;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
  margin: 0 0 5px 0;
}

.product-location {
  font-size: 12px;
  color: #999;
  margin: 0;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .nav-right {
    gap: 10px;
  }
  
  .category-list {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
  
  .content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
