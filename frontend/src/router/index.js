import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

// 路由组件
const Home = () => import('@/views/Home.vue')
const Login = () => import('@/views/auth/Login.vue')
const Register = () => import('@/views/auth/Register.vue')
const ProductList = () => import('@/views/product/ProductList.vue')
const ProductDetail = () => import('@/views/product/ProductDetail.vue')
const ProductPublish = () => import('@/views/product/ProductPublish.vue')
const UserCenter = () => import('@/views/user/UserCenter.vue')
const MessageCenter = () => import('@/views/message/MessageCenter.vue')
const OrderList = () => import('@/views/order/OrderList.vue')
const OrderCreate = () => import('@/views/order/OrderCreate.vue')
const UserProfile = () => import('@/views/user/Profile.vue')
const UserOrders = () => import('@/views/user/Orders.vue')
const UserProducts = () => import('@/views/user/Products.vue')
const AdminDashboard = () => import('@/views/admin/AdminDashboard.vue')
const NotifyCenter = () => import('@/views/notify/NotifyCenter.vue')
const AdminStats = () => import('@/views/admin/Stats.vue')

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录', requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册', requiresGuest: true }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: ProductList,
    meta: { title: '商品列表' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail,
    meta: { title: '商品详情' }
  },
  {
    path: '/publish',
    name: 'ProductPublish',
    component: ProductPublish,
    meta: { title: '发布商品', requiresAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: UserCenter,
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: UserProfile,
    meta: { title: '个人资料', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: OrderList,
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/order/create/:productId',
    name: 'OrderCreate',
    component: OrderCreate,
    meta: { title: '创建订单', requiresAuth: true }
  },
  {
    path: '/messages',
    name: 'MessageCenter',
    component: MessageCenter,
    meta: { title: '消息中心', requiresAuth: true }
  }
  ,
  {
    path: '/notifications',
    name: 'NotifyCenter',
    component: NotifyCenter,
    meta: { title: '系统通知', requiresAuth: true }
  }
  ,
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true }
  }
  ,
  {
    path: '/admin/stats',
    name: 'AdminStats',
    component: AdminStats,
    meta: { title: '数据统计', requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 校园二手交易平台` : '校园二手交易平台'
  
  const isAuthenticated = store.getters['auth/isAuthenticated']
  const currentUser = store.getters['auth/currentUser']
  const isAdmin = currentUser && currentUser.role === 'admin'
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
    return
  }
  
  // 已登录用户访问登录/注册页面
  if (to.meta.requiresGuest && isAuthenticated) {
    next('/')
    return
  }
  
  // 仅管理员访问
  if (to.meta.requiresAdmin && !isAdmin) {
    next('/')
    return
  }
  
  next()
})

export default router

