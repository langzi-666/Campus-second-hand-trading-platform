package com.campus.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.dto.MessageSendDTO;
import com.campus.trade.entity.Message;

import java.util.List;

/**
 * 消息服务接口
 * 
 * @author 开发团队
 */
public interface MessageService {
    
    /**
     * 发送消息
     * 
     * @param sendDTO 消息发送信息
     * @param senderId 发送者ID
     * @return 消息ID
     */
    Long sendMessage(MessageSendDTO sendDTO, Long senderId);
    
    /**
     * 分页查询会话消息
     * 
     * @param page 页码
     * @param size 每页大小
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param productId 商品ID
     * @return 消息分页列表
     */
    IPage<Message> getConversationMessages(Integer page, Integer size, Long userId1, Long userId2, Long productId);
    
    /**
     * 获取用户的会话列表
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Message> getUserConversations(Long userId);
    
    /**
     * 标记消息为已读
     * 
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @param productId 商品ID
     */
    void markMessagesAsRead(Long receiverId, Long senderId, Long productId);
    
    /**
     * 统计未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Integer countUnreadMessages(Long userId);
    
    /**
     * 获取最新消息
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param productId 商品ID
     * @return 最新消息
     */
    Message getLatestMessage(Long userId1, Long userId2, Long productId);
    
    /**
     * 发送系统消息
     * 
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @param orderId 关联订单ID
     */
    void sendSystemMessage(Long receiverId, String content, Long orderId);
}
