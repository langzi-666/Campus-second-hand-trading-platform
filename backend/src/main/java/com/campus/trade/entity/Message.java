package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体类
 * 
 * @author 开发团队
 */
@Data
@TableName("messages")
public class Message {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 关联商品ID
     */
    private Long productId;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 消息类型：1文本，2图片，3系统消息
     */
    @TableField("type")
    private Integer messageType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 图片URL（消息类型为图片时）
     */
    private String imageUrl;
    
    /**
     * 是否已读：0未读，1已读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
