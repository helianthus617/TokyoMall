package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.service.OrderItemService;

@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );
        return new PageUtils(page);
    }

    //    Queue 可以很多人都来监听只要收到消息后队列删除消息而且只能有一个收到消息
//    订单服务启动多个 同一个消息只能由一个客户端收到
//    只有一个消息完全处理完，方法运行结束，我们就可以接受到下一个消息
    @RabbitHandler
    public void receiveMessage1(Message message, OrderReturnReasonEntity orderReturnReasonEntity, Channel channel) {
        byte[] body = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("接收到的消息是" + message + "内容" + orderReturnReasonEntity);
    }

    @RabbitHandler
    public void receiveMessage2(Message message, OrderEntity orderEntity, Channel channel) {
        byte[] body = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("接收到的消息是" + message + "内容" + orderEntity);
//        通道内自增
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
//            签收
            channel.basicAck(deliveryTag,
                    false); //是否批量
//            拒绝签收
            channel.basicNack(deliveryTag,
                    false, //是否批量
                    false);  //是否重新入队
            channel.basicReject(deliveryTag, false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}