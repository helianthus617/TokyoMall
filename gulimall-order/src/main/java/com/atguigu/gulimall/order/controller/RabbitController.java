package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class RabbitController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public String sendMsg() {
        OrderReturnReasonEntity returnReasonEntity = new OrderReturnReasonEntity();
        rabbitTemplate.convertAndSend(
                "hello-java-exchange",
                "hello.java",
                returnReasonEntity);
        return "ok";
    }

}
