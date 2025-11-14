<template>
  <div class="message-center">
    <div class="container">
      <div class="message-layout">
        <!-- 会话列表 -->
        <div class="conversation-list">
          <div class="list-header">
            <h3>消息</h3>
            <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</span>
          </div>
          
          <div class="conversation-items" v-loading="conversationsLoading">
            <div
              v-for="conversation in conversations"
              :key="conversation.id"
              class="conversation-item"
              :class="{ active: selectedConversation?.id === conversation.id }"
              @click="selectConversation(conversation)"
            >
              <el-avatar :src="conversation.avatar" :size="50">
                {{ conversation.nickname?.charAt(0) }}
              </el-avatar>
              <div class="conversation-info">
                <div class="conversation-header">
                  <span class="nickname">{{ conversation.nickname }}</span>
                  <span class="time">{{ formatTime(conversation.lastMessageTime) }}</span>
                </div>
                <div class="last-message">
                  <span>{{ conversation.lastMessage }}</span>
                  <span v-if="conversation.unreadCount > 0" class="unread-count">
                    {{ conversation.unreadCount }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="conversations.length === 0 && !conversationsLoading" class="empty-conversations">
            <el-empty description="暂无消息" />
          </div>
        </div>
        
        <!-- 消息内容 -->
        <div class="message-content">
          <div v-if="selectedConversation" class="chat-container">
            <!-- 聊天头部 -->
            <div class="chat-header">
              <div class="chat-user">
                <el-avatar :src="selectedConversation.avatar" :size="40">
                  {{ selectedConversation.nickname?.charAt(0) }}
                </el-avatar>
                <div class="user-info">
                  <h4>{{ selectedConversation.nickname }}</h4>
                  <p v-if="selectedConversation.productTitle">
                    关于：{{ selectedConversation.productTitle }}
                  </p>
                </div>
              </div>
            </div>
            
            <!-- 消息列表 -->
            <div class="message-list" ref="messageListRef" v-loading="messagesLoading">
              <div
                v-for="message in messages"
                :key="message.id"
                class="message-item"
                :class="{ 'is-mine': message.isMine }"
              >
                <el-avatar 
                  v-if="!message.isMine"
                  :src="selectedConversation.avatar" 
                  :size="32"
                >
                  {{ selectedConversation.nickname?.charAt(0) }}
                </el-avatar>
                
                <div class="message-bubble">
                  <div v-if="message.messageType === 1" class="text-message">
                    {{ message.content }}
                  </div>
                  <div v-else-if="message.messageType === 2" class="image-message">
                    <img :src="message.imageUrl" :alt="message.content" @click="previewImage(message.imageUrl)">
                  </div>
                  <div v-else-if="message.messageType === 3" class="system-message">
                    {{ message.content }}
                  </div>
                  <div class="message-time">
                    {{ formatTime(message.createdAt) }}
                  </div>
                </div>
                
                <el-avatar 
                  v-if="message.isMine"
                  :src="currentUser?.avatar" 
                  :size="32"
                >
                  {{ currentUser?.nickname?.charAt(0) }}
                </el-avatar>
              </div>
            </div>
            
            <!-- 消息输入 -->
            <div class="message-input">
              <div class="input-toolbar">
                <el-button 
                  type="text" 
                  @click="handleImageUpload"
                  :disabled="sending"
                >
                  <el-icon><Picture /></el-icon>
                </el-button>
              </div>
              <div class="input-area">
                <el-input
                  v-model="messageText"
                  type="textarea"
                  :rows="3"
                  placeholder="输入消息..."
                  :disabled="sending"
                  @keydown.ctrl.enter="sendMessage"
                />
                <el-button
                  type="primary"
                  :loading="sending"
                  @click="sendMessage"
                  :disabled="!messageText.trim()"
                >
                  发送
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-else class="empty-chat">
            <el-empty description="选择一个会话开始聊天" />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 图片上传 -->
    <input
      ref="imageInputRef"
      type="file"
      accept="image/*"
      style="display: none"
      @change="handleImageSelect"
    />
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import api from '@/api'

export default {
  name: 'MessageCenter',
  setup() {
    const route = useRoute()
    const store = useStore()
    const messageListRef = ref()
    const imageInputRef = ref()
    
    const conversationsLoading = ref(false)
    const messagesLoading = ref(false)
    const sending = ref(false)
    const conversations = ref([])
    const messages = ref([])
    const selectedConversation = ref(null)
    const messageText = ref('')
    const unreadCount = ref(0)
    
    // 计算属性
    const currentUser = computed(() => store.getters['auth/currentUser'])
    
    // 加载会话列表
    const loadConversations = async () => {
      try {
        conversationsLoading.value = true
        
        // 暂时使用模拟数据
        const mockConversations = [
          {
            id: 1,
            userId: 2,
            nickname: '张三',
            avatar: '',
            productId: 1,
            productTitle: 'iPhone 13 128G 蓝色',
            lastMessage: '这个手机还在吗？',
            lastMessageTime: '2024-11-14T15:30:00',
            unreadCount: 2
          },
          {
            id: 2,
            userId: 3,
            nickname: '李四',
            avatar: '',
            productId: 2,
            productTitle: 'MacBook Air M1',
            lastMessage: '可以面交吗？',
            lastMessageTime: '2024-11-14T14:20:00',
            unreadCount: 0
          }
        ]
        
        conversations.value = mockConversations
        unreadCount.value = mockConversations.reduce((sum, conv) => sum + conv.unreadCount, 0)
        
        // 如果URL中有userId参数，自动选择对应会话
        if (route.query.userId) {
          const targetConversation = conversations.value.find(
            conv => conv.userId === parseInt(route.query.userId)
          )
          if (targetConversation) {
            selectConversation(targetConversation)
          }
        }
        
      } catch (error) {
        console.error('加载会话失败:', error)
        ElMessage.error('加载会话失败')
      } finally {
        conversationsLoading.value = false
      }
    }
    
    // 选择会话
    const selectConversation = (conversation) => {
      selectedConversation.value = conversation
      loadMessages(conversation)
      
      // 标记为已读
      if (conversation.unreadCount > 0) {
        markAsRead(conversation)
      }
    }
    
    // 加载消息
    const loadMessages = async (conversation) => {
      try {
        messagesLoading.value = true
        
        // 暂时使用模拟数据
        const mockMessages = [
          {
            id: 1,
            senderId: 2,
            content: '你好，这个手机还在吗？',
            messageType: 1,
            createdAt: '2024-11-14T15:00:00',
            isMine: false
          },
          {
            id: 2,
            senderId: currentUser.value?.id,
            content: '在的，9成新，功能都正常',
            messageType: 1,
            createdAt: '2024-11-14T15:05:00',
            isMine: true
          },
          {
            id: 3,
            senderId: 2,
            content: '可以看看实物吗？什么时候方便？',
            messageType: 1,
            createdAt: '2024-11-14T15:30:00',
            isMine: false
          }
        ]
        
        messages.value = mockMessages
        
        // 滚动到底部
        await nextTick()
        scrollToBottom()
        
      } catch (error) {
        console.error('加载消息失败:', error)
        ElMessage.error('加载消息失败')
      } finally {
        messagesLoading.value = false
      }
    }
    
    // 发送消息
    const sendMessage = async () => {
      if (!messageText.value.trim() || !selectedConversation.value) return
      
      try {
        sending.value = true
        
        const messageData = {
          receiverId: selectedConversation.value.userId,
          productId: selectedConversation.value.productId,
          messageType: 1,
          content: messageText.value.trim()
        }
        
        // 暂时模拟发送
        const newMessage = {
          id: Date.now(),
          senderId: currentUser.value?.id,
          content: messageText.value.trim(),
          messageType: 1,
          createdAt: new Date().toISOString(),
          isMine: true
        }
        
        messages.value.push(newMessage)
        messageText.value = ''
        
        // 更新会话列表中的最后消息
        const conversation = conversations.value.find(c => c.id === selectedConversation.value.id)
        if (conversation) {
          conversation.lastMessage = newMessage.content
          conversation.lastMessageTime = newMessage.createdAt
        }
        
        await nextTick()
        scrollToBottom()
        
        ElMessage.success('消息发送成功')
        
      } catch (error) {
        console.error('发送消息失败:', error)
        ElMessage.error('发送消息失败')
      } finally {
        sending.value = false
      }
    }
    
    // 处理图片上传
    const handleImageUpload = () => {
      imageInputRef.value?.click()
    }
    
    // 处理图片选择
    const handleImageSelect = async (event) => {
      const file = event.target.files[0]
      if (!file) return
      
      try {
        sending.value = true
        
        // 上传图片
        const formData = new FormData()
        formData.append('file', file)
        
        // 暂时模拟上传
        const imageUrl = URL.createObjectURL(file)
        
        const messageData = {
          receiverId: selectedConversation.value.userId,
          productId: selectedConversation.value.productId,
          messageType: 2,
          content: '发送了一张图片',
          imageUrl: imageUrl
        }
        
        const newMessage = {
          id: Date.now(),
          senderId: currentUser.value?.id,
          content: '发送了一张图片',
          messageType: 2,
          imageUrl: imageUrl,
          createdAt: new Date().toISOString(),
          isMine: true
        }
        
        messages.value.push(newMessage)
        
        await nextTick()
        scrollToBottom()
        
        ElMessage.success('图片发送成功')
        
      } catch (error) {
        console.error('发送图片失败:', error)
        ElMessage.error('发送图片失败')
      } finally {
        sending.value = false
        event.target.value = ''
      }
    }
    
    // 预览图片
    const previewImage = (imageUrl) => {
      // 可以使用Element Plus的图片预览组件
      window.open(imageUrl, '_blank')
    }
    
    // 标记为已读
    const markAsRead = (conversation) => {
      conversation.unreadCount = 0
      unreadCount.value = conversations.value.reduce((sum, conv) => sum + conv.unreadCount, 0)
    }
    
    // 滚动到底部
    const scrollToBottom = () => {
      if (messageListRef.value) {
        messageListRef.value.scrollTop = messageListRef.value.scrollHeight
      }
    }
    
    // 格式化时间
    const formatTime = (timeStr) => {
      const time = new Date(timeStr)
      const now = new Date()
      const diff = now - time
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) { // 1天内
        return Math.floor(diff / 3600000) + '小时前'
      } else {
        return time.toLocaleDateString()
      }
    }
    
    onMounted(() => {
      loadConversations()
    })
    
    return {
      messageListRef,
      imageInputRef,
      conversationsLoading,
      messagesLoading,
      sending,
      conversations,
      messages,
      selectedConversation,
      messageText,
      unreadCount,
      currentUser,
      selectConversation,
      sendMessage,
      handleImageUpload,
      handleImageSelect,
      previewImage,
      formatTime
    }
  }
}
</script>

<style scoped>
.message-center {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.message-layout {
  display: flex;
  height: 600px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.conversation-list {
  width: 300px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-header h3 {
  margin: 0;
  color: #333;
}

.unread-badge {
  background: #e74c3c;
  color: white;
  border-radius: 10px;
  padding: 2px 8px;
  font-size: 12px;
}

.conversation-items {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: background-color 0.3s;
  border-bottom: 1px solid #f5f5f5;
}

.conversation-item:hover {
  background-color: #f8f9fa;
}

.conversation-item.active {
  background-color: #e3f2fd;
}

.conversation-info {
  flex: 1;
  margin-left: 12px;
  overflow: hidden;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.nickname {
  font-weight: 500;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.last-message {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.last-message span:first-child {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.unread-count {
  background: #e74c3c;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 12px;
  margin-left: 8px;
}

.message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.chat-user {
  display: flex;
  align-items: center;
}

.user-info {
  margin-left: 12px;
}

.user-info h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.user-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.message-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-item {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

.message-item.is-mine {
  flex-direction: row-reverse;
}

.message-bubble {
  max-width: 60%;
  position: relative;
}

.text-message {
  background: #f1f1f1;
  padding: 10px 15px;
  border-radius: 18px;
  word-wrap: break-word;
}

.is-mine .text-message {
  background: #007aff;
  color: white;
}

.image-message img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
}

.system-message {
  background: #fff3cd;
  color: #856404;
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 12px;
  text-align: center;
}

.message-time {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin-top: 5px;
}

.message-input {
  border-top: 1px solid #eee;
  padding: 15px 20px;
}

.input-toolbar {
  margin-bottom: 10px;
}

.input-area {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.input-area .el-textarea {
  flex: 1;
}

.empty-conversations,
.empty-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

@media (max-width: 768px) {
  .container {
    padding: 0 10px;
  }
  
  .message-layout {
    height: 500px;
    flex-direction: column;
  }
  
  .conversation-list {
    width: 100%;
    height: 200px;
    border-right: none;
    border-bottom: 1px solid #eee;
  }
  
  .message-bubble {
    max-width: 80%;
  }
}
</style>
