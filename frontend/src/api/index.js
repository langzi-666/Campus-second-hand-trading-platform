import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = store.state.auth.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，则显示错误信息
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401未授权，跳转到登录页
      if (res.code === 401) {
        store.dispatch('auth/logout')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('请求错误:', error)
    
    let message = '网络错误'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          store.dispatch('auth/logout')
          router.push('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `连接错误${error.response.status}`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

// API接口定义
const api = {
  // 认证相关
  auth: {
    login: (data) => request.post('/auth/login', data),
    register: (data) => request.post('/auth/register', data),
    logout: () => request.post('/auth/logout'),
    checkUsername: (username) => request.get('/auth/check-username', { params: { username } }),
    checkEmail: (email) => request.get('/auth/check-email', { params: { email } })
  },
  
  // 用户相关
  user: {
    getProfile: () => request.get('/user/profile'),
    updateProfile: (data) => request.put('/user/profile', data),
    changePassword: (data) => request.put('/user/password', data),
    uploadAvatar: (data) => request.post('/user/avatar', data)
  },
  
  // 商品相关
  product: {
    getList: (params) => request.get('/products', { params }),
    getDetail: (id) => request.get(`/products/${id}`),
    create: (data) => request.post('/products', data),
    update: (id, data) => request.put(`/products/${id}`, data),
    delete: (id) => request.delete(`/products/${id}`),
    uploadImage: (data) => request.post('/products/upload', data)
  },
  
  // 分类相关
  category: {
    getList: () => request.get('/categories')
  },
  
  // 订单相关
  order: {
    getList: (params) => request.get('/orders', { params }),
    create: (data) => request.post('/orders', data),
    getDetail: (id) => request.get(`/orders/${id}`),
    updateStatus: (id, status) => request.put(`/orders/${id}/status`, { status })
  },
  
  // 消息相关
  message: {
    getList: (params) => request.get('/messages', { params }),
    send: (data) => request.post('/messages', data),
    markRead: (id) => request.put(`/messages/${id}/read`)
  }
}

export default api
