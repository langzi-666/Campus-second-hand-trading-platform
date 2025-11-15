import { getFavorites, toggleFavorite } from '@/mock/data'

const state = {
  favorites: []
}

const mutations = {
  SET_FAVORITES(state, list) {
    state.favorites = list || []
  }
}

const actions = {
  load({ commit, rootGetters }) {
    const user = rootGetters['auth/currentUser']
    if (!user || !user.id) {
      commit('SET_FAVORITES', [])
      return
    }
    const list = getFavorites(user.id)
    commit('SET_FAVORITES', list)
  },
  toggle({ dispatch, rootGetters }, productId) {
    const user = rootGetters['auth/currentUser']
    if (!user || !user.id) return false
    toggleFavorite(user.id, productId)
    dispatch('load')
    return true
  }
}

const getters = {
  list: state => state.favorites,
  isFavorite: state => id => state.favorites.includes(Number(id))
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
