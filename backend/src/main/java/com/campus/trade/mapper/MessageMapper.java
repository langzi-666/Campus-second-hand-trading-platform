package com.campus.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息Mapper接口
 * 
 * @author 开发团队
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 分页查询会话消息
     * 
     * @param page 分页参数
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param productId 商品ID
     * @return 消息分页列表
     */
    IPage<Message> selectConversationMessages(Page<Message> page,
                                            @Param("userId1") Long userId1,
                                            @Param("userId2") Long userId2,
                                            @Param("productId") Long productId);
    
    /**
     * 获取用户的会话列表
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Message> selectUserConversations(@Param("userId") Long userId);
    
    /**
     * 标记消息为已读
     * 
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @param productId 商品ID
     */
    void markMessagesAsRead(@Param("receiverId") Long receiverId,
                          @Param("senderId") Long senderId,
                          @Param("productId") Long productId);
    
    /**
     * 统计未读消息数量
     * 
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Integer countUnreadMessages(@Param("userId") Long userId);
    
    /**
     * 获取两个用户之间的最新消息
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param productId 商品ID
     * @return 最新消息
     */
    Message selectLatestMessage(@Param("userId1") Long userId1,
                              @Param("userId2") Long userId2,
                              @Param("productId") Long productId);
}
