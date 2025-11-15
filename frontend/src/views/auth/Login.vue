<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>
    
    <!-- 左侧信息面板 -->
    <div class="info-panel">
      <div class="info-content">
        <div class="logo-section">
          <div class="logo-icon">🎓</div>
          <h1 class="platform-title">校园二手交易平台</h1>
          <p class="platform-subtitle">Campus Second-hand Trading Platform</p>
        </div>
        
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">🛍️</div>
            <div class="feature-text">
              <h3>安全交易</h3>
              <p>校园内部交易，安全可靠</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">💬</div>
            <div class="feature-text">
              <h3>即时沟通</h3>
              <p>买卖双方实时消息交流</p>
            </div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">📱</div>
            <div class="feature-text">
              <h3>便捷管理</h3>
              <p>个人中心一站式管理</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 右侧登录表单 -->
    <div class="login-panel">
      <div class="login-box">
        <div class="login-header">
          <h2>欢迎回来</h2>
          <p>登录您的账户继续使用</p>
        </div>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <div class="input-wrapper">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名或邮箱"
                prefix-icon="User"
                size="large"
                class="custom-input"
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <div class="input-wrapper">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                show-password
                class="custom-input"
                @keyup.enter="handleLogin"
              />
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              <span v-if="!loading">立即登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
          
          <div class="login-footer">
            <span>还没有账号？</span>
            <router-link to="/register" class="register-link">
              立即注册
            </router-link>
          </div>
          
          <!-- 快速登录提示 -->
          <div class="quick-login-tip">
            <el-divider>
              <span class="tip-text">测试账号</span>
            </el-divider>
            <div class="test-accounts">
              <div class="account-item" @click="fillTestAccount('admin')">
                <span class="account-label">管理员</span>
                <span class="account-info">admin / 123456</span>
              </div>
              <div class="account-item" @click="fillTestAccount('student1')">
                <span class="account-label">学生1</span>
                <span class="account-info">student1 / 123456</span>
              </div>
            </div>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const store = useStore()
    const loginFormRef = ref()
    const loading = ref(false)
    
    // 表单数据
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    // 表单验证规则
    const loginRules = {
      username: [
        { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }
    
    // 处理登录
    const handleLogin = async () => {
      if (!loginFormRef.value) return
      
      try {
        await loginFormRef.value.validate()
        loading.value = true
        
        await store.dispatch('auth/login', loginForm)
        
        ElMessage.success('登录成功')
        router.push('/')
        
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
    
    // 填充测试账号
    const fillTestAccount = (type) => {
      if (type === 'admin') {
        loginForm.username = 'admin'
        loginForm.password = '123456'
      } else if (type === 'student1') {
        loginForm.username = 'student1'
        loginForm.password = '123456'
      }
      ElMessage.info('已填充测试账号，点击登录即可')
    }
    
    return {
      loginFormRef,
      loginForm,
      loginRules,
      loading,
      handleLogin,
      fillTestAccount
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 70%;
  left: 80%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 30%;
  left: 85%;
  animation-delay: 4s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  top: 80%;
  left: 5%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 左侧信息面板 */
.info-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 2;
}

.info-content {
  max-width: 500px;
  color: white;
}

.logo-section {
  text-align: center;
  margin-bottom: 60px;
}

.logo-icon {
  font-size: 80px;
  margin-bottom: 20px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

.platform-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.platform-subtitle {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 300;
  letter-spacing: 1px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateX(10px);
  background: rgba(255, 255, 255, 0.15);
}

.feature-icon {
  font-size: 32px;
  min-width: 50px;
}

.feature-text h3 {
  font-size: 18px;
  margin-bottom: 5px;
  font-weight: 600;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

/* 右侧登录面板 */
.login-panel {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  position: relative;
  z-index: 2;
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 50px 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  color: #333;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 700;
}

.login-header p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.login-form {
  width: 100%;
}

.input-wrapper {
  margin-bottom: 20px;
}

.custom-input {
  border-radius: 12px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
  border-color: #667eea;
}

.login-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  font-size: 14px;
  color: #666;
}

.register-link {
  color: #667eea;
  text-decoration: none;
  margin-left: 5px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.register-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 快速登录提示 */
.quick-login-tip {
  margin-top: 30px;
}

.tip-text {
  color: #999;
  font-size: 12px;
  padding: 0 15px;
  background: rgba(255, 255, 255, 0.95);
}

.test-accounts {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.account-item {
  flex: 1;
  padding: 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  border: 1px solid #dee2e6;
}

.account-item:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.account-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 4px;
}

.account-info {
  display: block;
  font-size: 11px;
  opacity: 0.8;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .info-panel {
    display: none;
  }
  
  .login-panel {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .login-box {
    padding: 30px 20px;
  }
  
  .test-accounts {
    flex-direction: column;
  }
}
</style>
