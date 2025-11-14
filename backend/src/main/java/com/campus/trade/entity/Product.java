package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 
 * @author 开发团队
 */
@Data
@TableName("products")
public class Product {
    
    /**
     * 商品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 发布用户ID
     */
    private Long userId;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 商品图片，JSON格式存储
     */
    private String images;
    
    /**
     * 新旧程度：1全新，2几乎全新，3轻微使用，4明显使用，5重度使用
     */
    private Integer conditionLevel;
    
    /**
     * 交易地点
     */
    private String location;
    
    /**
     * 联系方式
     */
    private String contactInfo;
    
    /**
     * 状态：1在售，2已售，3下架，4删除
     */
    private Integer status;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 收藏次数
     */
    private Integer favoriteCount;
    
    /**
     * 是否推荐：1是，0否
     */
    private Integer isFeatured;
    
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
