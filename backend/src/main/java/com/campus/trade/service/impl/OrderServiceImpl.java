package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.dto.OrderCreateDTO;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.Product;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单服务实现类
 * 
 * @author 开发团队
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    @Transactional
    public Long createOrder(OrderCreateDTO createDTO, Long buyerId) {
        // 检查商品是否存在且可购买
        Product product = productMapper.selectById(createDTO.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品不可购买");
        }
        if (product.getUserId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }
        
        // 创建订单
        Order order = new Order();
        BeanUtils.copyProperties(createDTO, order);
        
        // 生成订单号
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getUserId());
        order.setProductTitle(product.getTitle());
        
        // 获取商品第一张图片作为快照
        try {
            String images = product.getImages();
            if (images != null && !images.isEmpty()) {
                String[] imageArray = objectMapper.readValue(images, String[].class);
                if (imageArray.length > 0) {
                    order.setProductImage(imageArray[0]);
                }
            }
        } catch (JsonProcessingException e) {
            // 忽略图片处理错误
        }
        
        order.setStatus(1); // 待确认状态
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        orderMapper.insert(order);
        return order.getId();
    }
    
    @Override
    @Transactional
    public boolean confirmOrder(Long orderId, Long sellerId, String sellerContact) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不允许确认");
        }
        
        order.setStatus(2); // 已确认状态
        order.setSellerContact(sellerContact);
        order.setSellerConfirmAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional
    public boolean completeOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw new RuntimeException("订单状态不允许完成");
        }
        
        order.setStatus(4); // 已完成状态
        order.setCompletedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // 更新商品状态为已售出
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            product.setStatus(2); // 已售出
            productMapper.updateById(product);
        }
        
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权限操作此订单");
        }
        if (order.getStatus() == 4 || order.getStatus() == 5) {
            throw new RuntimeException("订单状态不允许取消");
        }
        
        order.setStatus(5); // 已取消状态
        order.setCancelReason(reason);
        order.setCancelledAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    public Order getOrderById(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        
        // 检查权限
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权限查看此订单");
        }
        
        return order;
    }
    
    @Override
    public IPage<Order> getBuyerOrderPage(Integer page, Integer size, Long buyerId, Integer status) {
        Page<Order> pageParam = new Page<>(page, size);
        return orderMapper.selectBuyerOrderPage(pageParam, buyerId, status);
    }
    
    @Override
    public IPage<Order> getSellerOrderPage(Integer page, Integer size, Long sellerId, Integer status) {
        Page<Order> pageParam = new Page<>(page, size);
        return orderMapper.selectSellerOrderPage(pageParam, sellerId, status);
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }
    
    @Override
    public Integer countUserOrders(Long userId, Integer status) {
        return orderMapper.countUserOrders(userId, status);
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "CT" + timestamp + random;
    }
}
