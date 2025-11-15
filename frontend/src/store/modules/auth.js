import api from '@/api'

const state = {
  token: localStorage.getItem('token') || '',
  user: JSON.parse(localStorage.getItem('user') || '{}')
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
    if (token) {
      localStorage.setItem('token', token)
    } else {
      localStorage.removeItem('token')
    }
  },
  
  SET_USER(state, user) {
    state.user = user
    if (user && Object.keys(user).length > 0) {
      localStorage.setItem('user', JSON.stringify(user))
    } else {
      localStorage.removeItem('user')
    }
  },
  
  CLEAR_AUTH(state) {
    state.token = ''
    state.user = {}
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}

const actions = {
  // 登录
  async login({ commit }, loginForm) {
    const map = {
      admin: { id: 1, username: 'admin', nickname: '管理员', role: 'admin', avatar: '' },
      student1: { id: 2, username: 'student1', nickname: '学生1', role: 'user', avatar: '' }
    }
    if (map[loginForm.username] && loginForm.password === '123456') {
      const token = `mock-token-${loginForm.username}`
      const user = map[loginForm.username]
      const response = { code: 200, data: { token, user } }
      commit('SET_TOKEN', token)
      commit('SET_USER', user)
      return response
    }
    try {
      const response = await api.auth.login(loginForm)
      const { token, user } = response.data
      commit('SET_TOKEN', token)
      commit('SET_USER', user)
      return response
    } catch (error) {
      throw error
    }
  },
  
  // 注册
  async register({ commit }, registerForm) {
    try {
      const response = await api.auth.register(registerForm)
      return response
    } catch (error) {
      throw error
    }
  },
  
  // 登出
  logout({ commit }) {
    commit('CLEAR_AUTH')
  },
  
  // 更新用户信息
  updateUser({ commit }, user) {
    commit('SET_USER', user)
  }
}

const getters = {
  isAuthenticated: state => !!state.token,
  currentUser: state => state.user,
  token: state => state.token
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
