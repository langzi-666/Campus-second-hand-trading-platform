package com.campus.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trade.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品Mapper接口
 * 
 * @author 开发团队
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 分页查询商品列表
     * 
     * @param page 分页参数
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     * @param userId 用户ID
     * @return 商品分页列表
     */
    IPage<Product> selectProductPage(Page<Product> page, 
                                   @Param("categoryId") Long categoryId,
                                   @Param("keyword") String keyword,
                                   @Param("userId") Long userId);
    
    /**
     * 获取推荐商品
     * 
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    List<Product> selectFeaturedProducts(@Param("limit") Integer limit);
    
    /**
     * 根据用户ID获取商品列表
     * 
     * @param userId 用户ID
     * @return 用户商品列表
     */
    List<Product> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 更新浏览次数
     * 
     * @param productId 商品ID
     */
    void updateViewCount(@Param("productId") Long productId);
}
