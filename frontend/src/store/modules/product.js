import api from '@/api'

const state = {
  products: [],
  categories: [],
  currentProduct: null
}

const mutations = {
  SET_PRODUCTS(state, products) {
    state.products = products
  },
  
  SET_CATEGORIES(state, categories) {
    state.categories = categories
  },
  
  SET_CURRENT_PRODUCT(state, product) {
    state.currentProduct = product
  }
}

const actions = {
  // 获取商品列表
  async getProducts({ commit }, params) {
    try {
      const response = await api.product.getList(params)
      commit('SET_PRODUCTS', response.data)
      return response
    } catch (error) {
      throw error
    }
  },
  
  // 获取商品分类
  async getCategories({ commit }) {
    try {
      const response = await api.category.getList()
      commit('SET_CATEGORIES', response.data)
      return response
    } catch (error) {
      throw error
    }
  },
  
  // 获取商品详情
  async getProductDetail({ commit }, id) {
    try {
      const response = await api.product.getDetail(id)
      commit('SET_CURRENT_PRODUCT', response.data)
      return response
    } catch (error) {
      throw error
    }
  }
}

const getters = {
  products: state => state.products,
  categories: state => state.categories,
  currentProduct: state => state.currentProduct
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
