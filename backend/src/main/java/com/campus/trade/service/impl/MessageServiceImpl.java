package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.dto.MessageSendDTO;
import com.campus.trade.entity.Message;
import com.campus.trade.mapper.MessageMapper;
import com.campus.trade.service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息服务实现类
 * 
 * @author 开发团队
 */
@Service
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Override
    public Long sendMessage(MessageSendDTO sendDTO, Long senderId) {
        Message message = new Message();
        BeanUtils.copyProperties(sendDTO, message);
        
        message.setSenderId(senderId);
        message.setIsRead(0); // 未读状态
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        messageMapper.insert(message);
        return message.getId();
    }
    
    @Override
    public IPage<Message> getConversationMessages(Integer page, Integer size, Long userId1, Long userId2, Long productId) {
        Page<Message> pageParam = new Page<>(page, size);
        return messageMapper.selectConversationMessages(pageParam, userId1, userId2, productId);
    }
    
    @Override
    public List<Message> getUserConversations(Long userId) {
        return messageMapper.selectUserConversations(userId);
    }
    
    @Override
    public void markMessagesAsRead(Long receiverId, Long senderId, Long productId) {
        messageMapper.markMessagesAsRead(receiverId, senderId, productId);
    }
    
    @Override
    public Integer countUnreadMessages(Long userId) {
        return messageMapper.countUnreadMessages(userId);
    }
    
    @Override
    public Message getLatestMessage(Long userId1, Long userId2, Long productId) {
        return messageMapper.selectLatestMessage(userId1, userId2, productId);
    }
    
    @Override
    public void sendSystemMessage(Long receiverId, String content, Long orderId) {
        Message message = new Message();
        message.setSenderId(0L); // 系统消息发送者ID为0
        message.setReceiverId(receiverId);
        message.setOrderId(orderId);
        message.setMessageType(3); // 系统消息类型
        message.setContent(content);
        message.setIsRead(0); // 未读状态
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        messageMapper.insert(message);
    }
}
