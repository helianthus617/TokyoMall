package com.atguigu.gulimall.seckill.scheduled;

import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.service.impl.SeckillServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

//秒杀商品上架 每晚三点 上架最近三天的商品
//        当天 00:00:00 - 23:59:59
//        明天 00:00:00 - 23:59:59
//        后天 00:00:00 - 23:59:59
@Slf4j
@Service
public class SeckillSkuScheduled {
    /**
     * 这里应该是幂等的
     * 三秒执行一次：* /3 * * * * ?
     * 8小时执行一次：0 0 0-8 * * ?
     */
//    @Scheduled(cron = "0 0 0-8 * * ?")
//    @Scheduled(cron="0 0 3 * * ?") 每晚三点
//    @Scheduled(cron="*/3 * * * * ? ")
//    @Scheduled(cron="0 * * * * ?")
//    @Scheduled(cron="0 0 3 * * ?") // 每晚三点
    @Autowired
    SeckillServiceImpl seckillService;
    @Autowired
    private RedissonClient redissonClient;
    private final String upload_lock = "seckill:upload:lock";

    /**
     * 这里应该是幂等的
     * 三秒执行一次：* /3 * * * * ?
     * 8小时执行一次：0 0 0-8 * * ?
     */
//    @Scheduled(cron = "0 0 0-8 * * ?")
//    @Scheduled(cron="0 0 3 * * ?") 每晚三点
//    @Scheduled(cron="*/3 * * * * ? ")
    @Scheduled(cron = "* * 3 * * ?")
    public void uploadSeckillSkuLatest3Day() {
        log.info("\n上架秒杀商品的信息");
        // 1.重复上架无需处理 加上分布式锁 状态已经更新 释放锁以后其他人才获取到最新状态
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(100, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckillSkuLatest3Day();
        } finally {
            lock.unlock();
        }

    }
}