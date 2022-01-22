package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {
    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    public void createExchange() {
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功", "hello-java-exchange");
    }

    @Test
    public void createQueue() {
        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功", "hello-java-queue");
    }

    @Test
    public void createBinding() {
        Binding binding = new Binding(
                "hello-java-queue",
                Binding.DestinationType.QUEUE,
                "hello-java-exchange",
                "hello.java",
                null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功", "binding");
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void sendMsg() {
        OrderReturnReasonEntity returnReasonEntity = new OrderReturnReasonEntity();
        returnReasonEntity.setId(1L);
        returnReasonEntity.setCreateTime(new Date());
        returnReasonEntity.setName("哈哈");
        String msg = "Hello World";

        rabbitTemplate.convertAndSend(
                "hello-java-exchange",
                "hello.java",
                returnReasonEntity
                , new CorrelationData(UUID.randomUUID().toString()));
        log.info("消息发送完成", returnReasonEntity);
    }
}













