package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 *
 * @author ****
 * @email none
 * @date 2021-12-16 18:00:07
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

}
