package com.atguigu.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@EnableAsync
//@EnableScheduling
@Component
@Slf4j
public class HelloSchedule {
    /**
     * 1 spring中6位组成
     * 秒-分-时-日-月-周  周和日一般其中一个位？ 通配符
     * @Scheduled 开启一个定时任务
     * 在周几的位置1-7 代表周一到周日
     * 定时任务不应该阻塞，默认是阻塞的
     * 异步执行
     * 1), 可以让业务以异步运行 completablefuture
     * 2), 支持定时任务线程池 设置 spring.task.scheduling.pool.size=5
     * 3), 让定时任务异步执行
     * @EnableAsync 给希望异步执行的 标注注解@Async
     **/
//    @Async
//    @Scheduled(cron = "*/3 * * * * ? ")
//    public void hello() throws InterruptedException {
//        Thread.sleep(5000);
//        log.info("hello");
//    }
}