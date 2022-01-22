package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author ****
 * @email none
 * @date 2021-12-16 18:00:07
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

}
