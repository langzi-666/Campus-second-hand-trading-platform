<template>
  <div class="user-profile">
    <div class="container">
      <div class="profile-header">
        <h2>个人中心</h2>
        <p>管理您的个人信息和账户设置</p>
      </div>
      
      <div class="profile-content">
        <el-row :gutter="30">
          <!-- 左侧个人信息 -->
          <el-col :span="16">
            <div class="card fade-in">
              <h3>个人信息</h3>
              
              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="100px"
                class="profile-form"
              >
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="profileForm.username" disabled />
                </el-form-item>
                
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" disabled />
                </el-form-item>
                
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                </el-form-item>
                
                <el-form-item label="学号" prop="studentId">
                  <el-input v-model="profileForm.studentId" placeholder="请输入学号" />
                </el-form-item>
                
                <el-form-item label="学校" prop="school">
                  <el-input v-model="profileForm.school" placeholder="请输入学校" />
                </el-form-item>
                
                <el-form-item label="学院" prop="college">
                  <el-input v-model="profileForm.college" placeholder="请输入学院" />
                </el-form-item>
                
                <el-form-item label="专业" prop="major">
                  <el-input v-model="profileForm.major" placeholder="请输入专业" />
                </el-form-item>
                
                <el-form-item label="年级" prop="grade">
                  <el-input v-model="profileForm.grade" placeholder="请输入年级" />
                </el-form-item>
                
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="2">女</el-radio>
                    <el-radio :label="0">保密</el-radio>
                  </el-radio-group>
                </el-form-item>
                
                <el-form-item>
                  <el-button
                    type="primary"
                    :loading="updating"
                    @click="handleUpdateProfile"
                  >
                    保存信息
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <!-- 修改密码 -->
            <div class="card fade-in">
              <h3>修改密码</h3>
              
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
                class="password-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入原密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item>
                  <el-button
                    type="primary"
                    :loading="changingPassword"
                    @click="handleChangePassword"
                  >
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-col>
          
          <!-- 右侧头像和统计 -->
          <el-col :span="8">
            <!-- 头像上传 -->
            <div class="card fade-in">
              <h3>头像</h3>
              
              <div class="avatar-section">
                <el-avatar
                  :src="userInfo.avatar"
                  :size="120"
                  class="user-avatar"
                >
                  {{ userInfo.nickname?.charAt(0) }}
                </el-avatar>
                
                <el-upload
                  ref="uploadRef"
                  :action="uploadAction"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :on-error="handleAvatarError"
                  :before-upload="beforeAvatarUpload"
                  accept="image/*"
                  class="avatar-upload"
                >
                  <el-button type="primary" size="small">
                    更换头像
                  </el-button>
                </el-upload>
              </div>
            </div>
            
            <!-- 统计信息 -->
            <div class="card fade-in">
              <h3>我的统计</h3>
              
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-number">{{ stats.productCount }}</div>
                  <div class="stat-label">发布商品</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ stats.orderCount }}</div>
                  <div class="stat-label">订单数量</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ stats.messageCount }}</div>
                  <div class="stat-label">消息数量</div>
                </div>
                <div class="stat-item">
                  <div class="stat-number">{{ stats.favoriteCount }}</div>
                  <div class="stat-label">收藏数量</div>
                </div>
              </div>
            </div>
            
            <!-- 快捷操作 -->
            <div class="card fade-in">
              <h3>快捷操作</h3>
              
              <div class="quick-actions">
                <el-button
                  type="primary"
                  @click="$router.push('/publish')"
                  class="action-btn"
                >
                  发布商品
                </el-button>
                <el-button
                  type="success"
                  @click="$router.push('/orders')"
                  class="action-btn"
                >
                  我的订单
                </el-button>
                <el-button
                  type="info"
                  @click="$router.push('/messages')"
                  class="action-btn"
                >
                  消息中心
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'UserProfile',
  setup() {
    const store = useStore()
    const profileFormRef = ref()
    const passwordFormRef = ref()
    const uploadRef = ref()
    
    const updating = ref(false)
    const changingPassword = ref(false)
    const userInfo = ref({})
    const stats = ref({
      productCount: 0,
      orderCount: 0,
      messageCount: 0,
      favoriteCount: 0
    })
    
    // 个人信息表单
    const profileForm = reactive({
      username: '',
      email: '',
      nickname: '',
      studentId: '',
      school: '',
      college: '',
      major: '',
      grade: '',
      phone: '',
      gender: 0
    })
    
    // 密码表单
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    // 表单验证规则
    const profileRules = {
      nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' }
      ],
      phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ]
    }
    
    const passwordRules = {
      oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== passwordForm.newPassword) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }
    
    // 计算属性
    const uploadAction = computed(() => {
      return `/api/user/avatar`
    })
    
    const uploadHeaders = computed(() => {
      const token = store.getters['auth/token']
      return {
        Authorization: `Bearer ${token}`
      }
    })
    
    // 加载用户信息
    const loadUserProfile = async () => {
      try {
        const response = await api.user.getProfile()
        if (response.code === 200) {
          userInfo.value = response.data
          Object.assign(profileForm, response.data)
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
        ElMessage.error('加载用户信息失败')
      }
    }
    
    // 加载统计信息
    const loadUserStats = async () => {
      try {
        const response = await api.user.getUserStats()
        if (response.code === 200) {
          stats.value = response.data
        }
      } catch (error) {
        console.error('加载统计信息失败:', error)
      }
    }
    
    // 更新个人信息
    const handleUpdateProfile = async () => {
      if (!profileFormRef.value) return
      
      try {
        await profileFormRef.value.validate()
        updating.value = true
        
        const response = await api.user.updateProfile(profileForm)
        if (response.code === 200) {
          ElMessage.success('个人信息更新成功')
          await loadUserProfile()
        } else {
          ElMessage.error(response.message || '更新失败')
        }
      } catch (error) {
        console.error('更新个人信息失败:', error)
        if (error !== false) {
          ElMessage.error('更新个人信息失败')
        }
      } finally {
        updating.value = false
      }
    }
    
    // 修改密码
    const handleChangePassword = async () => {
      if (!passwordFormRef.value) return
      
      try {
        await passwordFormRef.value.validate()
        changingPassword.value = true
        
        const response = await api.user.changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        
        if (response.code === 200) {
          ElMessage.success('密码修改成功')
          // 清空表单
          Object.assign(passwordForm, {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
          })
        } else {
          ElMessage.error(response.message || '密码修改失败')
        }
      } catch (error) {
        console.error('修改密码失败:', error)
        if (error !== false) {
          ElMessage.error('修改密码失败')
        }
      } finally {
        changingPassword.value = false
      }
    }
    
    // 头像上传前检查
    const beforeAvatarUpload = (file) => {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        ElMessage.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    }
    
    // 头像上传成功
    const handleAvatarSuccess = (response) => {
      if (response.code === 200) {
        userInfo.value.avatar = response.data
        ElMessage.success('头像更新成功')
      } else {
        ElMessage.error(response.message || '头像上传失败')
      }
    }
    
    // 头像上传失败
    const handleAvatarError = () => {
      ElMessage.error('头像上传失败')
    }
    
    onMounted(() => {
      loadUserProfile()
      loadUserStats()
    })
    
    return {
      profileFormRef,
      passwordFormRef,
      uploadRef,
      updating,
      changingPassword,
      userInfo,
      stats,
      profileForm,
      passwordForm,
      profileRules,
      passwordRules,
      uploadAction,
      uploadHeaders,
      handleUpdateProfile,
      handleChangePassword,
      beforeAvatarUpload,
      handleAvatarSuccess,
      handleAvatarError
    }
  }
}
</script>

<style scoped>
.user-profile {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.profile-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.profile-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
}

.profile-header p {
  color: #666;
  font-size: 16px;
}

.profile-content {
  margin-top: 20px;
}

.card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 24px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.card h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.profile-form,
.password-form {
  margin-top: 20px;
}

.avatar-section {
  text-align: center;
}

.user-avatar {
  margin-bottom: 20px;
  border: 3px solid #667eea;
}

.avatar-upload {
  display: block;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-btn {
  width: 100%;
  border-radius: 25px;
  padding: 12px 24px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .profile-header {
    padding: 20px 15px;
  }
  
  .card {
    padding: 20px 15px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
