package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.dto.ProductCreateDTO;
import com.campus.trade.entity.Product;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品服务实现类
 * 
 * @author 开发团队
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Long createProduct(ProductCreateDTO createDTO, Long userId) {
        Product product = new Product();
        BeanUtils.copyProperties(createDTO, product);
        
        product.setUserId(userId);
        product.setStatus(1); // 默认在售状态
        product.setViewCount(0);
        product.setFavoriteCount(0);
        product.setIsFeatured(0);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        // 将图片列表转换为JSON字符串
        if (createDTO.getImages() != null && !createDTO.getImages().isEmpty()) {
            try {
                product.setImages(objectMapper.writeValueAsString(createDTO.getImages()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("图片信息处理失败", e);
            }
        }
        
        productMapper.insert(product);
        return product.getId();
    }
    
    @Override
    public boolean updateProduct(Long productId, ProductCreateDTO createDTO, Long userId) {
        // 检查商品是否存在且属于当前用户
        Product existingProduct = productMapper.selectById(productId);
        if (existingProduct == null || !existingProduct.getUserId().equals(userId)) {
            throw new RuntimeException("商品不存在或无权限修改");
        }
        
        Product product = new Product();
        BeanUtils.copyProperties(createDTO, product);
        product.setId(productId);
        product.setUpdatedAt(LocalDateTime.now());
        
        // 将图片列表转换为JSON字符串
        if (createDTO.getImages() != null && !createDTO.getImages().isEmpty()) {
            try {
                product.setImages(objectMapper.writeValueAsString(createDTO.getImages()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("图片信息处理失败", e);
            }
        }
        
        return productMapper.updateById(product) > 0;
    }
    
    @Override
    public boolean deleteProduct(Long productId, Long userId) {
        // 检查商品是否存在且属于当前用户
        Product existingProduct = productMapper.selectById(productId);
        if (existingProduct == null || !existingProduct.getUserId().equals(userId)) {
            throw new RuntimeException("商品不存在或无权限删除");
        }
        
        // 软删除，更新状态为删除
        Product product = new Product();
        product.setId(productId);
        product.setStatus(4); // 删除状态
        product.setUpdatedAt(LocalDateTime.now());
        
        return productMapper.updateById(product) > 0;
    }
    
    @Override
    public Product getProductById(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product != null && product.getStatus() != 4) {
            // 更新浏览次数
            productMapper.updateViewCount(productId);
            return product;
        }
        return null;
    }
    
    @Override
    public IPage<Product> getProductPage(Integer page, Integer size, Long categoryId, String keyword) {
        Page<Product> pageParam = new Page<>(page, size);
        return productMapper.selectProductPage(pageParam, categoryId, keyword, null);
    }
    
    @Override
    public List<Product> getFeaturedProducts(Integer limit) {
        return productMapper.selectFeaturedProducts(limit);
    }
    
    @Override
    public List<Product> getUserProducts(Long userId) {
        return productMapper.selectByUserId(userId);
    }
    
    @Override
    public boolean updateProductStatus(Long productId, Integer status, Long userId) {
        // 检查商品是否存在且属于当前用户
        Product existingProduct = productMapper.selectById(productId);
        if (existingProduct == null || !existingProduct.getUserId().equals(userId)) {
            throw new RuntimeException("商品不存在或无权限修改");
        }
        
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        product.setUpdatedAt(LocalDateTime.now());
        
        return productMapper.updateById(product) > 0;
    }
}
