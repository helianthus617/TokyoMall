package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 *
 * @author ****
 * @email none
 * @date 2021-12-16 18:02:09
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
