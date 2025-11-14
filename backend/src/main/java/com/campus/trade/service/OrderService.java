package com.campus.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.dto.OrderCreateDTO;
import com.campus.trade.entity.Order;

/**
 * 订单服务接口
 * 
 * @author 开发团队
 */
public interface OrderService {
    
    /**
     * 创建订单
     * 
     * @param createDTO 订单创建信息
     * @param buyerId 买家ID
     * @return 订单ID
     */
    Long createOrder(OrderCreateDTO createDTO, Long buyerId);
    
    /**
     * 卖家确认订单
     * 
     * @param orderId 订单ID
     * @param sellerId 卖家ID
     * @param sellerContact 卖家联系方式
     * @return 是否成功
     */
    boolean confirmOrder(Long orderId, Long sellerId, String sellerContact);
    
    /**
     * 完成订单
     * 
     * @param orderId 订单ID
     * @param userId 操作用户ID
     * @return 是否成功
     */
    boolean completeOrder(Long orderId, Long userId);
    
    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @param userId 操作用户ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId, Long userId, String reason);
    
    /**
     * 获取订单详情
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 订单信息
     */
    Order getOrderById(Long orderId, Long userId);
    
    /**
     * 分页查询买家订单
     * 
     * @param page 页码
     * @param size 每页大小
     * @param buyerId 买家ID
     * @param status 订单状态
     * @return 订单分页列表
     */
    IPage<Order> getBuyerOrderPage(Integer page, Integer size, Long buyerId, Integer status);
    
    /**
     * 分页查询卖家订单
     * 
     * @param page 页码
     * @param size 每页大小
     * @param sellerId 卖家ID
     * @param status 订单状态
     * @return 订单分页列表
     */
    IPage<Order> getSellerOrderPage(Integer page, Integer size, Long sellerId, Integer status);
    
    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 统计用户订单数量
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单数量
     */
    Integer countUserOrders(Long userId, Integer status);
}
