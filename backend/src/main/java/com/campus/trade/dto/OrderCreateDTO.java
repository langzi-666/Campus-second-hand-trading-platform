package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 订单创建DTO
 * 
 * @author 开发团队
 */
@Data
public class OrderCreateDTO {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 交易价格
     */
    @NotNull(message = "交易价格不能为空")
    @DecimalMin(value = "0.01", message = "交易价格必须大于0")
    private BigDecimal price;
    
    /**
     * 交易方式：1面交，2邮寄
     */
    @NotNull(message = "交易方式不能为空")
    private Integer tradeMethod;
    
    /**
     * 交易地点
     */
    @Size(max = 200, message = "交易地点长度不能超过200个字符")
    private String tradeLocation;
    
    /**
     * 买家联系方式
     */
    @NotBlank(message = "联系方式不能为空")
    @Size(max = 500, message = "联系方式长度不能超过500个字符")
    private String buyerContact;
    
    /**
     * 备注
     */
    @Size(max = 1000, message = "备注长度不能超过1000个字符")
    private String remark;
}
