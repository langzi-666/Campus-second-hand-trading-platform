package com.campus.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 * 
 * @author 开发团队
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 分页查询买家订单
     * 
     * @param page 分页参数
     * @param buyerId 买家ID
     * @param status 订单状态
     * @return 订单分页列表
     */
    IPage<Order> selectBuyerOrderPage(Page<Order> page, 
                                    @Param("buyerId") Long buyerId,
                                    @Param("status") Integer status);
    
    /**
     * 分页查询卖家订单
     * 
     * @param page 分页参数
     * @param sellerId 卖家ID
     * @param status 订单状态
     * @return 订单分页列表
     */
    IPage<Order> selectSellerOrderPage(Page<Order> page, 
                                     @Param("sellerId") Long sellerId,
                                     @Param("status") Integer status);
    
    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 查询用户参与的所有订单
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectUserOrders(@Param("userId") Long userId);
    
    /**
     * 统计用户订单数量
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单数量
     */
    Integer countUserOrders(@Param("userId") Long userId, @Param("status") Integer status);
}
