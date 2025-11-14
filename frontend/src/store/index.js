import { createStore } from 'vuex'
import auth from './modules/auth'
import user from './modules/user'
import product from './modules/product'

export default createStore({
  modules: {
    auth,
    user,
    product
  },
  
  state: {
    loading: false
  },
  
  mutations: {
    SET_LOADING(state, loading) {
      state.loading = loading
    }
  },
  
  actions: {
    setLoading({ commit }, loading) {
      commit('SET_LOADING', loading)
    }
  },
  
  getters: {
    loading: state => state.loading
  }
})
