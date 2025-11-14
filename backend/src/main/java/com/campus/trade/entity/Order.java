package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 
 * @author 开发团队
 */
@Data
@TableName("orders")
public class Order {
    
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 买家ID
     */
    private Long buyerId;
    
    /**
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品标题快照
     */
    private String productTitle;
    
    /**
     * 商品图片快照
     */
    private String productImage;
    
    /**
     * 交易价格
     */
    private BigDecimal price;
    
    /**
     * 交易方式：1面交，2邮寄
     */
    private Integer tradeMethod;
    
    /**
     * 交易地点
     */
    private String tradeLocation;
    
    /**
     * 买家联系方式
     */
    private String buyerContact;
    
    /**
     * 卖家联系方式
     */
    private String sellerContact;
    
    /**
     * 状态：1待确认，2已确认，3交易中，4已完成，5已取消
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 买家确认时间
     */
    private LocalDateTime buyerConfirmAt;
    
    /**
     * 卖家确认时间
     */
    private LocalDateTime sellerConfirmAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    /**
     * 取消时间
     */
    private LocalDateTime cancelledAt;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
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
