package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 *
 * @author ****
 * @email none
 * @date 2021-12-16 18:02:09
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {

}
