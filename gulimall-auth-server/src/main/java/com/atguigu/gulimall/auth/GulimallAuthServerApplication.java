package com.atguigu.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

//springsession使用
//    导入包
//        <dependency>
//        <groupId>org.springframework.session</groupId>
//        <artifactId>spring-session-data-redis</artifactId>
//        </dependency>
//    配置
//        spring.redis.session.store-type=redis
//        server.servlet.session.timeout=30m
//    开启session @EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisHttpSession
public class GulimallAuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }
}