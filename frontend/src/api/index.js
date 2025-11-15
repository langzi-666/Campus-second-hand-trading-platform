import axios from 'axios'
import { ElMessage } from 'element-plus'
let injectedStore = null
let injectedRouter = null

export const inject = ({ store, router }) => {
  injectedStore = store
  injectedRouter = router
}

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = injectedStore?.state?.auth?.token
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
        injectedStore?.dispatch('auth/logout')
        injectedRouter?.push('/login')
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
          injectedStore?.dispatch('auth/logout')
          injectedRouter?.push('/login')
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
    changePassword: (data) => request.put('/user/password', null, { params: data }),
    uploadAvatar: (data) => request.post('/user/avatar', data),
    getUserStats: () => request.get('/user/stats')
  },
  
  // 商品相关API
  product: {
    getProducts: (params) => request.get('/products', { params }),
    getProduct: (id) => request.get(`/products/${id}`),
    createProduct: (data) => request.post('/products', data),
    updateProduct: (id, data) => request.put(`/products/${id}`, data),
    deleteProduct: (id) => request.delete(`/products/${id}`),
    getFeaturedProducts: () => request.get('/products/featured'),
    getMyProducts: () => request.get('/products/my'),
    updateProductStatus: (id, status) => request.put(`/products/${id}/status`, { status })
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
  
  // 消息相关API
  message: {
    // 发送消息
    send: (data) => request.post('/messages', data),
    
    // 获取会话消息
    getConversationMessages: (params) => request.get('/messages/conversation', { params }),
    
    // 获取会话列表
    getConversations: () => request.get('/messages/conversations'),
    
    // 标记消息为已读
    markAsRead: (params) => request.put('/messages/read', null, { params }),
    
    // 获取未读消息数量
    getUnreadCount: () => request.get('/messages/unread/count'),
    
    // 获取最新消息
    getLatestMessage: (params) => request.get('/messages/latest', { params })
  }
}

export default api
