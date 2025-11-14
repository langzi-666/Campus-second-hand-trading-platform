package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 消息发送DTO
 * 
 * @author 开发团队
 */
@Data
public class MessageSendDTO {
    
    /**
     * 接收者ID
     */
    @NotNull(message = "接收者ID不能为空")
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
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;
    
    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容长度不能超过2000个字符")
    private String content;
    
    /**
     * 图片URL（消息类型为图片时）
     */
    private String imageUrl;
}
