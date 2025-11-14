package com.campus.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.trade.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类Mapper接口
 * 
 * @author 开发团队
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 获取所有启用的分类
     * 
     * @return 分类列表
     */
    List<Category> selectActiveCategories();
    
    /**
     * 根据父分类ID获取子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> selectByParentId(Long parentId);
}
