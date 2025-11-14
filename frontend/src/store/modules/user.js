import api from '@/api'

const state = {
  profile: {}
}

const mutations = {
  SET_PROFILE(state, profile) {
    state.profile = profile
  }
}

const actions = {
  // 获取用户资料
  async getProfile({ commit }) {
    try {
      const response = await api.user.getProfile()
      commit('SET_PROFILE', response.data)
      return response
    } catch (error) {
      throw error
    }
  },
  
  // 更新用户资料
  async updateProfile({ commit }, profileData) {
    try {
      const response = await api.user.updateProfile(profileData)
      commit('SET_PROFILE', response.data)
      return response
    } catch (error) {
      throw error
    }
  }
}

const getters = {
  profile: state => state.profile
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
