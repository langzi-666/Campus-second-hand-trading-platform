const key = (userId) => `notify:${userId}`

const ls = typeof window !== 'undefined' ? window.localStorage : {
  getItem() { return null }, setItem() {}, removeItem() {}
}

function loadFromLS(userId) {
  const raw = ls.getItem(key(userId))
  try { return raw ? JSON.parse(raw) : [] } catch { return [] }
}

function saveToLS(userId, list) {
  ls.setItem(key(userId), JSON.stringify(list))
}

function genSamples(userId) {
  const now = Date.now()
  return [
    { id: now - 1, title: '系统通知', content: '欢迎使用校园二手交易平台！', read: false, createdAt: new Date(now - 60000).toISOString() },
    { id: now - 2, title: '交易提醒', content: '有人对你的商品感兴趣，快去看看吧。', read: false, createdAt: new Date(now - 120000).toISOString() },
    { id: now - 3, title: '安全提示', content: '线下交易请注意安全，尽量在人多的场所完成交易。', read: true, createdAt: new Date(now - 3600000).toISOString() }
  ]
}

const state = {
  items: []
}

const mutations = {
  SET_ITEMS(state, list) {
    state.items = Array.isArray(list) ? list : []
  }
}

const actions = {
  init({ commit, rootGetters }) {
    const user = rootGetters['auth/currentUser']
    if (!user || !user.id) {
      commit('SET_ITEMS', [])
      return
    }
    let list = loadFromLS(user.id)
    if (!list || list.length === 0) {
      list = genSamples(user.id)
      saveToLS(user.id, list)
    }
    commit('SET_ITEMS', list)
  },
  markAllRead({ commit, state, rootGetters }) {
    const user = rootGetters['auth/currentUser']
    if (!user || !user.id) return
    const list = state.items.map(it => ({ ...it, read: true }))
    saveToLS(user.id, list)
    commit('SET_ITEMS', list)
  },
  add({ commit, state, rootGetters }, payload) {
    const user = rootGetters['auth/currentUser']
    if (!user || !user.id) return
    const item = { id: Date.now(), read: false, createdAt: new Date().toISOString(), ...payload }
    const list = [item, ...state.items]
    saveToLS(user.id, list)
    commit('SET_ITEMS', list)
  }
}

const getters = {
  list: state => state.items,
  unreadCount: state => state.items.filter(it => !it.read).length
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
