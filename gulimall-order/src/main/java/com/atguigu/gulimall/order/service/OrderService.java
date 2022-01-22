package com.atguigu.gulimall.order.service;

import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.gulimall.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    void closeOrder(OrderEntity entity);

    OrderEntity getOrderByOrderSn(String orderSn);

    PayVo getOrderPay(String orderSn);

    String handlePayResult(PayAsyncVo vo);

    PageUtils queryPageWithItems(Map<String, Object> params);

    void createSeckillOrder(SeckillOrderTo orderTo);
}

