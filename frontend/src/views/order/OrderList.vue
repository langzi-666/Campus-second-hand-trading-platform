<template>
  <div class="order-list">
    <div class="container">
      <div class="list-header">
        <h2>我的订单</h2>
        <div class="order-tabs">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="全部" name="all" />
            <el-tab-pane label="待确认" name="1" />
            <el-tab-pane label="已确认" name="2" />
            <el-tab-pane label="交易中" name="3" />
            <el-tab-pane label="已完成" name="4" />
            <el-tab-pane label="已取消" name="5" />
          </el-tabs>
        </div>
      </div>
      
      <div class="order-type-tabs">
        <el-radio-group v-model="orderType" @change="handleTypeChange">
          <el-radio-button label="buyer">我的购买</el-radio-button>
          <el-radio-button label="seller">我的销售</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="order-content" v-loading="loading">
        <div v-if="orders.length > 0" class="order-items">
          <div
            v-for="order in orders"
            :key="order.id"
            class="order-item"
            @click="handleOrderClick(order)"
          >
            <div class="order-header">
              <div class="order-info">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ formatTime(order.createdAt) }}</span>
              </div>
              <div class="order-status">
                <el-tag :type="getStatusType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="order-body">
              <div class="product-info">
                <img :src="order.productImage || '/placeholder.svg'" :alt="order.productTitle" class="product-image">
                <div class="product-details">
                  <h4>{{ order.productTitle }}</h4>
                  <p class="trade-method">{{ getTradeMethodText(order.tradeMethod) }}</p>
                  <p v-if="order.tradeLocation" class="trade-location">{{ order.tradeLocation }}</p>
                </div>
              </div>
              <div class="order-price">
                <span class="price">¥{{ order.price }}</span>
              </div>
            </div>
            
            <div class="order-actions">
              <el-button 
                v-if="order.status === 1 && orderType === 'seller'"
                type="primary" 
                size="small"
                @click.stop="handleConfirmOrder(order)"
              >
                确认订单
              </el-button>
              <el-button 
                v-if="(order.status === 2 || order.status === 3)"
                type="success" 
                size="small"
                @click.stop="handleCompleteOrder(order)"
              >
                完成交易
              </el-button>
              <el-button 
                v-if="order.status === 1 || order.status === 2"
                type="danger" 
                size="small"
                @click.stop="handleCancelOrder(order)"
              >
                取消订单
              </el-button>
              <el-button 
                size="small"
                @click.stop="handleContactUser(order)"
              >
                联系{{ orderType === 'buyer' ? '卖家' : '买家' }}
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-state">
          <el-empty description="暂无订单" />
        </div>
        
        <!-- 分页 -->
        <div v-if="total > 0" class="pagination">
          <el-pagination
            v-model:current-page="searchParams.page"
            v-model:page-size="searchParams.size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadOrders"
            @current-change="loadOrders"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

export default {
  name: 'OrderList',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const orders = ref([])
    const total = ref(0)
    const activeTab = ref('all')
    const orderType = ref('buyer')
    
    // 搜索参数
    const searchParams = reactive({
      page: 1,
      size: 10,
      status: null
    })
    
    // 状态映射
    const statusMap = {
      1: '待确认',
      2: '已确认', 
      3: '交易中',
      4: '已完成',
      5: '已取消'
    }
    
    const statusTypeMap = {
      1: 'warning',
      2: 'primary',
      3: 'info',
      4: 'success',
      5: 'danger'
    }
    
    const tradeMethodMap = {
      1: '面交',
      2: '邮寄'
    }
    
    // 加载订单列表
    const loadOrders = async () => {
      try {
        loading.value = true
        
        // 暂时使用模拟数据
        const mockData = {
          records: [
            {
              id: 1,
              orderNo: 'CT20241114150030001',
              productTitle: 'iPhone 13 128G 蓝色',
              productImage: '',
              price: 4500,
              tradeMethod: 1,
              tradeLocation: '宿舍楼下',
              status: 1,
              createdAt: '2024-11-14T15:00:30'
            },
            {
              id: 2,
              orderNo: 'CT20241113120015002',
              productTitle: 'MacBook Air M1',
              productImage: '',
              price: 6800,
              tradeMethod: 1,
              tradeLocation: '图书馆',
              status: 2,
              createdAt: '2024-11-13T12:00:15'
            }
          ],
          total: 2,
          current: 1,
          size: 10
        }
        
        orders.value = mockData.records
        total.value = mockData.total
        
      } catch (error) {
        console.error('加载订单失败:', error)
        ElMessage.error('加载订单失败')
      } finally {
        loading.value = false
      }
    }
    
    // 切换标签
    const handleTabChange = (tab) => {
      searchParams.status = tab === 'all' ? null : parseInt(tab)
      searchParams.page = 1
      loadOrders()
    }
    
    // 切换订单类型
    const handleTypeChange = () => {
      searchParams.page = 1
      loadOrders()
    }
    
    // 点击订单
    const handleOrderClick = (order) => {
      router.push(`/order/${order.id}`)
    }
    
    // 确认订单
    const handleConfirmOrder = async (order) => {
      try {
        const { value: sellerContact } = await ElMessageBox.prompt(
          '请输入您的联系方式',
          '确认订单',
          {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            inputPlaceholder: '请输入微信号、QQ号或手机号',
            inputValidator: (value) => {
              if (!value) {
                return '联系方式不能为空'
              }
              return true
            }
          }
        )
        
        // 调用确认订单API
        ElMessage.success('订单确认成功')
        loadOrders()
        
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('确认订单失败')
        }
      }
    }
    
    // 完成订单
    const handleCompleteOrder = async (order) => {
      try {
        await ElMessageBox.confirm(
          '确定要完成这个订单吗？完成后无法撤销。',
          '完成订单',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 调用完成订单API
        ElMessage.success('订单完成成功')
        loadOrders()
        
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('完成订单失败')
        }
      }
    }
    
    // 取消订单
    const handleCancelOrder = async (order) => {
      try {
        const { value: reason } = await ElMessageBox.prompt(
          '请输入取消原因',
          '取消订单',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            inputPlaceholder: '请输入取消原因（可选）'
          }
        )
        
        // 调用取消订单API
        ElMessage.success('订单取消成功')
        loadOrders()
        
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('取消订单失败')
        }
      }
    }
    
    // 联系用户
    const handleContactUser = (order) => {
      const targetUserId = orderType.value === 'buyer' ? order.sellerId : order.buyerId
      router.push(`/messages?userId=${targetUserId}&productId=${order.productId}`)
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      return statusMap[status] || '未知'
    }
    
    // 获取状态类型
    const getStatusType = (status) => {
      return statusTypeMap[status] || 'info'
    }
    
    // 获取交易方式文本
    const getTradeMethodText = (method) => {
      return tradeMethodMap[method] || '未知'
    }
    
    // 格式化时间
    const formatTime = (timeStr) => {
      return new Date(timeStr).toLocaleString()
    }
    
    onMounted(() => {
      loadOrders()
    })
    
    return {
      loading,
      orders,
      total,
      activeTab,
      orderType,
      searchParams,
      handleTabChange,
      handleTypeChange,
      handleOrderClick,
      handleConfirmOrder,
      handleCompleteOrder,
      handleCancelOrder,
      handleContactUser,
      getStatusText,
      getStatusType,
      getTradeMethodText,
      formatTime,
      loadOrders
    }
  }
}
</script>

<style scoped>
.order-list {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.list-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.list-header h2 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 24px;
}

.order-type-tabs {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  text-align: center;
}

.order-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.order-item {
  padding: 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.order-item:hover {
  background-color: #f8f9fa;
}

.order-item:last-child {
  border-bottom: none;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.order-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.order-no {
  font-weight: 500;
  color: #333;
}

.order-time {
  color: #999;
  font-size: 14px;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.product-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
}

.product-details h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.trade-method,
.trade-location {
  margin: 2px 0;
  color: #666;
  font-size: 14px;
}

.order-price {
  text-align: right;
}

.price {
  font-size: 18px;
  font-weight: bold;
  color: #e74c3c;
}

.order-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
}

.pagination {
  padding: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .order-body {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .order-actions {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .order-actions .el-button {
    flex: 1;
    min-width: 80px;
  }
}
</style>
