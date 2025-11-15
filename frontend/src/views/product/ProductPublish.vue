<template>
  <div class="product-publish">
    <div class="container">
      <div class="publish-header">
        <h2>发布商品</h2>
        <p>请填写商品信息，发布您的二手商品</p>
      </div>
      
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="productRules"
        label-width="100px"
        class="product-form"
      >
        <el-form-item label="商品标题" prop="title">
          <el-input
            v-model="productForm.title"
            placeholder="请输入商品标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="商品分类" prop="categoryId">
          <el-select
            v-model="productForm.categoryId"
            placeholder="请选择商品分类"
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="商品价格" prop="price">
          <el-input-number
            v-model="productForm.price"
            :min="0.01"
            :precision="2"
            placeholder="请输入价格"
            style="width: 200px"
          />
          <span class="price-unit">元</span>
        </el-form-item>
        
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number
            v-model="productForm.originalPrice"
            :min="0.01"
            :precision="2"
            placeholder="请输入原价（可选）"
            style="width: 200px"
          />
          <span class="price-unit">元</span>
        </el-form-item>
        
        <el-form-item label="新旧程度" prop="conditionLevel">
          <el-radio-group v-model="productForm.conditionLevel">
            <el-radio :label="1">全新</el-radio>
            <el-radio :label="2">几乎全新</el-radio>
            <el-radio :label="3">轻微使用</el-radio>
            <el-radio :label="4">明显使用</el-radio>
            <el-radio :label="5">重度使用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="商品图片" prop="images">
          <el-upload
            ref="uploadRef"
            :file-list="fileList"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            action="/api/files/upload"
            list-type="picture-card"
            :limit="9"
            multiple
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                只能上传jpg/png文件，且不超过5MB，最多9张图片
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="6"
            placeholder="请详细描述商品的状况、购买时间、使用情况等"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="交易地点" prop="location">
          <el-input
            v-model="productForm.location"
            placeholder="请输入交易地点，如：宿舍楼下、图书馆等"
            maxlength="200"
          />
        </el-form-item>
        
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input
            v-model="productForm.contactInfo"
            placeholder="请输入联系方式，如：微信号、QQ号等"
            maxlength="500"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSubmit"
          >
            发布商品
          </el-button>
          <el-button
            size="large"
            @click="handleReset"
          >
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'ProductPublish',
  setup() {
    const router = useRouter()
    const productFormRef = ref()
    const uploadRef = ref()
    const loading = ref(false)
    const fileList = ref([])
    const categories = ref([])
    
    // 表单数据
    const productForm = reactive({
      title: '',
      categoryId: null,
      price: null,
      originalPrice: null,
      conditionLevel: 1,
      images: [],
      description: '',
      location: '',
      contactInfo: ''
    })
    
    // 表单验证规则
    const productRules = {
      title: [
        { required: true, message: '请输入商品标题', trigger: 'blur' },
        { max: 200, message: '标题长度不能超过200个字符', trigger: 'blur' }
      ],
      categoryId: [
        { required: true, message: '请选择商品分类', trigger: 'change' }
      ],
      price: [
        { required: true, message: '请输入商品价格', trigger: 'blur' },
        { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
      ],
      conditionLevel: [
        { required: true, message: '请选择新旧程度', trigger: 'change' }
      ],
      description: [
        { max: 2000, message: '描述长度不能超过2000个字符', trigger: 'blur' }
      ],
      location: [
        { max: 200, message: '交易地点长度不能超过200个字符', trigger: 'blur' }
      ],
      contactInfo: [
        { max: 500, message: '联系方式长度不能超过500个字符', trigger: 'blur' }
      ]
    }
    
    // 加载商品分类
    const loadCategories = async () => {
      try {
        // 暂时使用模拟数据
        categories.value = [
          { id: 1, name: '数码产品' },
          { id: 2, name: '学习用品' },
          { id: 3, name: '生活用品' },
          { id: 4, name: '服装鞋帽' },
          { id: 5, name: '运动器材' },
          { id: 6, name: '其他' }
        ]
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }
    
    // 文件上传前检查
    const beforeUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
      }
      if (!isLt5M) {
        ElMessage.error('图片大小不能超过 5MB!')
        return false
      }
      return true
    }
    
    // 文件上传成功
    const handleUploadSuccess = (response, file) => {
      if (response.code === 200) {
        productForm.images.push(response.data)
        ElMessage.success('图片上传成功')
      } else {
        ElMessage.error(response.message || '图片上传失败')
      }
    }
    
    // 移除文件
    const handleRemove = (file, fileList) => {
      // 从images数组中移除对应的URL
      const index = productForm.images.findIndex(url => url === file.response?.data)
      if (index > -1) {
        productForm.images.splice(index, 1)
      }
    }
    
    // 提交表单
    const handleSubmit = async () => {
      if (!productFormRef.value) return
      
      try {
        await productFormRef.value.validate()
        loading.value = true
        
        await api.product.createProduct(productForm)
        
        ElMessage.success('商品发布成功')
        router.push('/user')
        
      } catch (error) {
        console.error('发布失败:', error)
        ElMessage.error(error.message || '发布失败')
      } finally {
        loading.value = false
      }
    }
    
    // 重置表单
    const handleReset = () => {
      if (productFormRef.value) {
        productFormRef.value.resetFields()
      }
      productForm.images = []
      fileList.value = []
      if (uploadRef.value) {
        uploadRef.value.clearFiles()
      }
    }
    
    onMounted(() => {
      loadCategories()
    })
    
    return {
      productFormRef,
      uploadRef,
      productForm,
      productRules,
      loading,
      fileList,
      categories,
      beforeUpload,
      handleUploadSuccess,
      handleRemove,
      handleSubmit,
      handleReset
    }
  }
}
</script>

<style scoped>
.product-publish {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.publish-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.publish-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
}

.publish-header p {
  color: #666;
  font-size: 14px;
}

.product-form {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.price-unit {
  margin-left: 10px;
  color: #666;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .product-form {
    padding: 20px 15px;
  }
  
  .publish-header {
    padding: 20px 15px;
  }
}
</style>
