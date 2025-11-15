import api from '@/api'

/**
 * 消息轮询工具
 */
class MessagePolling {
  constructor() {
    this.polling = false
    this.interval = null
    this.callbacks = []
  }
  
  /**
   * 开始轮询
   * @param {number} intervalTime 轮询间隔（毫秒）
   */
  start(intervalTime = 5000) {
    if (this.polling) return
    
    this.polling = true
    this.interval = setInterval(async () => {
      try {
        const response = await api.message.getUnreadCount()
        if (response.code === 200) {
          this.notifyCallbacks(response.data)
        }
      } catch (error) {
        console.error('轮询未读消息失败:', error)
      }
    }, intervalTime)
  }
  
  /**
   * 停止轮询
   */
  stop() {
    if (!this.polling) return
    
    this.polling = false
    if (this.interval) {
      clearInterval(this.interval)
      this.interval = null
    }
  }
  
  /**
   * 添加回调函数
   * @param {Function} callback 回调函数
   */
  addCallback(callback) {
    this.callbacks.push(callback)
  }
  
  /**
   * 移除回调函数
   * @param {Function} callback 回调函数
   */
  removeCallback(callback) {
    const index = this.callbacks.indexOf(callback)
    if (index > -1) {
      this.callbacks.splice(index, 1)
    }
  }
  
  /**
   * 通知所有回调函数
   * @param {number} unreadCount 未读消息数量
   */
  notifyCallbacks(unreadCount) {
    this.callbacks.forEach(callback => {
      try {
        callback(unreadCount)
      } catch (error) {
        console.error('消息回调执行失败:', error)
      }
    })
  }
}

// 创建单例实例
const messagePolling = new MessagePolling()

export default messagePolling
