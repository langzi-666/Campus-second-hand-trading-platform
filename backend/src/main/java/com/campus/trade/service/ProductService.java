package com.campus.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.trade.dto.ProductCreateDTO;
import com.campus.trade.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 * 
 * @author 开发团队
 */
public interface ProductService {
    
    /**
     * 创建商品
     * 
     * @param createDTO 商品创建信息
     * @param userId 用户ID
     * @return 商品ID
     */
    Long createProduct(ProductCreateDTO createDTO, Long userId);
    
    /**
     * 更新商品
     * 
     * @param productId 商品ID
     * @param createDTO 商品更新信息
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateProduct(Long productId, ProductCreateDTO createDTO, Long userId);
    
    /**
     * 删除商品
     * 
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteProduct(Long productId, Long userId);
    
    /**
     * 获取商品详情
     * 
     * @param productId 商品ID
     * @return 商品信息
     */
    Product getProductById(Long productId);
    
    /**
     * 分页查询商品列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     * @return 商品分页列表
     */
    IPage<Product> getProductPage(Integer page, Integer size, Long categoryId, String keyword);
    
    /**
     * 获取推荐商品
     * 
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    List<Product> getFeaturedProducts(Integer limit);
    
    /**
     * 获取用户商品列表
     * 
     * @param userId 用户ID
     * @return 用户商品列表
     */
    List<Product> getUserProducts(Long userId);
    
    /**
     * 更新商品状态
     * 
     * @param productId 商品ID
     * @param status 状态
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateProductStatus(Long productId, Integer status, Long userId);
}
