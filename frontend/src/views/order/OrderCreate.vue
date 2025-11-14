<template>
  <div class="order-create">
    <div class="container">
      <div class="create-header">
        <h2>创建订单</h2>
        <p>请确认商品信息并填写交易详情</p>
      </div>
      
      <div class="order-content" v-if="product">
        <!-- 商品信息 -->
        <div class="product-info">
          <h3>商品信息</h3>
          <div class="product-card">
            <img :src="getProductImage(product)" :alt="product.title" class="product-image">
            <div class="product-details">
              <h4>{{ product.title }}</h4>
              <p class="product-price">¥{{ product.price }}</p>
              <p class="product-condition">{{ getConditionText(product.conditionLevel) }}</p>
              <p class="product-location">{{ product.location }}</p>
            </div>
          </div>
        </div>
        
        <!-- 订单表单 -->
        <el-form
          ref="orderFormRef"
          :model="orderForm"
          :rules="orderRules"
          label-width="120px"
          class="order-form"
        >
          <el-form-item label="交易价格" prop="price">
            <el-input-number
              v-model="orderForm.price"
              :min="0.01"
              :precision="2"
              :max="product.price"
              placeholder="请输入交易价格"
              style="width: 200px"
            />
            <span class="price-note">（建议价格：¥{{ product.price }}）</span>
          </el-form-item>
          
          <el-form-item label="交易方式" prop="tradeMethod">
            <el-radio-group v-model="orderForm.tradeMethod">
              <el-radio :label="1">面交</el-radio>
              <el-radio :label="2">邮寄</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item 
            label="交易地点" 
            prop="tradeLocation"
            v-if="orderForm.tradeMethod === 1"
          >
            <el-input
              v-model="orderForm.tradeLocation"
              placeholder="请输入交易地点，如：宿舍楼下、图书馆等"
              maxlength="200"
            />
          </el-form-item>
          
          <el-form-item label="联系方式" prop="buyerContact">
            <el-input
              v-model="orderForm.buyerContact"
              type="textarea"
              :rows="3"
              placeholder="请输入您的联系方式，如：微信号、QQ号、手机号等"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="orderForm.remark"
              type="textarea"
              :rows="4"
              placeholder="请输入备注信息，如：交易时间偏好、其他要求等"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleSubmit"
            >
              创建订单
            </el-button>
            <el-button
              size="large"
              @click="handleCancel"
            >
              取消
            </el-button>
          </el-form-item>
        </el-form>
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'OrderCreate',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const orderFormRef = ref()
    const loading = ref(false)
    const product = ref(null)
    
    // 表单数据
    const orderForm = reactive({
      productId: null,
      price: null,
      tradeMethod: 1,
      tradeLocation: '',
      buyerContact: '',
      remark: ''
    })
    
    // 表单验证规则
    const orderRules = {
      price: [
        { required: true, message: '请输入交易价格', trigger: 'blur' },
        { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
      ],
      tradeMethod: [
        { required: true, message: '请选择交易方式', trigger: 'change' }
      ],
      tradeLocation: [
        { required: true, message: '请输入交易地点', trigger: 'blur' },
        { max: 200, message: '交易地点长度不能超过200个字符', trigger: 'blur' }
      ],
      buyerContact: [
        { required: true, message: '请输入联系方式', trigger: 'blur' },
        { max: 500, message: '联系方式长度不能超过500个字符', trigger: 'blur' }
      ],
      remark: [
        { max: 1000, message: '备注长度不能超过1000个字符', trigger: 'blur' }
      ]
    }
    
    // 新旧程度映射
    const conditionMap = {
      1: '全新',
      2: '几乎全新',
      3: '轻微使用',
      4: '明显使用',
      5: '重度使用'
    }
    
    // 加载商品信息
    const loadProduct = async () => {
      try {
        loading.value = true
        const productId = route.params.productId
        
        // 暂时使用模拟数据
        const mockProduct = {
          id: parseInt(productId),
          title: 'iPhone 13 128G 蓝色',
          price: 4500,
          conditionLevel: 2,
          location: '宿舍楼下',
          images: '[]'
        }
        
        product.value = mockProduct
        orderForm.productId = mockProduct.id
        orderForm.price = mockProduct.price
        
      } catch (error) {
        console.error('加载商品信息失败:', error)
        ElMessage.error('加载商品信息失败')
      } finally {
        loading.value = false
      }
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
    
    // 提交订单
    const handleSubmit = async () => {
      if (!orderFormRef.value) return
      
      try {
        await orderFormRef.value.validate()
        loading.value = true
        
        // 暂时模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        ElMessage.success('订单创建成功')
        router.push('/user/orders')
        
      } catch (error) {
        console.error('创建订单失败:', error)
        if (error !== false) { // 不是表单验证错误
          ElMessage.error(error.message || '创建订单失败')
        }
      } finally {
        loading.value = false
      }
    }
    
    // 取消创建
    const handleCancel = () => {
      router.go(-1)
    }
    
    onMounted(() => {
      loadProduct()
    })
    
    return {
      orderFormRef,
      loading,
      product,
      orderForm,
      orderRules,
      getProductImage,
      getConditionText,
      handleSubmit,
      handleCancel
    }
  }
}
</script>

<style scoped>
.order-create {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.create-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.create-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
}

.create-header p {
  color: #666;
  font-size: 14px;
}

.order-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.product-info {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.product-info h3 {
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
}

.product-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.product-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 20px;
}

.product-details h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
}

.product-price {
  margin: 5px 0;
  color: #e74c3c;
  font-size: 18px;
  font-weight: bold;
}

.product-condition,
.product-location {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.order-form {
  margin-top: 20px;
}

.price-note {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
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
  
  .order-content {
    padding: 20px 15px;
  }
  
  .create-header {
    padding: 20px 15px;
  }
  
  .product-card {
    flex-direction: column;
    text-align: center;
  }
  
  .product-image {
    margin-right: 0;
    margin-bottom: 15px;
  }
}
</style>
