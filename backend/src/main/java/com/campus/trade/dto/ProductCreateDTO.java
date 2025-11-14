package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品创建DTO
 * 
 * @author 开发团队
 */
@Data
public class ProductCreateDTO {
    
    /**
     * 分类ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    
    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    /**
     * 商品描述
     */
    @Size(max = 2000, message = "描述长度不能超过2000个字符")
    private String description;
    
    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 商品图片URL列表
     */
    private List<String> images;
    
    /**
     * 新旧程度：1全新，2几乎全新，3轻微使用，4明显使用，5重度使用
     */
    @NotNull(message = "新旧程度不能为空")
    private Integer conditionLevel;
    
    /**
     * 交易地点
     */
    @Size(max = 200, message = "交易地点长度不能超过200个字符")
    private String location;
    
    /**
     * 联系方式
     */
    @Size(max = 500, message = "联系方式长度不能超过500个字符")
    private String contactInfo;
}
