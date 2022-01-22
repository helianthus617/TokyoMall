package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();
        model.addAttribute("categorys", categoryEntityList);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    // 测试基本锁
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {

        // 1. 获取一把锁，只要锁的名字一样就是同一把锁
        // 2. 解决了锁的自动续期，看门狗机   如果业务超长,运行期间自动给锁续上新的30s
        // 3. 如果程序出现异常没有执行解锁代码，redisson也会执行解锁代码
        //阻塞式等待
        RLock lock = redissonClient.getLock("my-lock");
        lock.lock();
        //        10秒后自动解锁，自动解锁时间一定要大于业务执行时间，不执行看门狗机制
        //        lock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
// 2. 如果程序出现异常没有执行解锁代码，redisson也会执行解锁代码(断电)
            System.out.println("释放锁" + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }


    // 测试读写锁  只要写锁存在 就得等待写锁执行完成，才能执行读锁（读写锁一定能读到最新数据）
//    写锁是排他锁，读锁是一个共享锁
//    写锁没释放，读就必须等待
//    写+ 写
//    写+ 读
//    读+ 写 有读锁写锁也需要等待
//    读+ 读 相当于无锁
    @ResponseBody
    @GetMapping("/write")
    public String writeValue() {
//        改数据加写锁，读数据加读锁
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = lock.writeLock();
        try {
            rLock.lock();
            s = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    // 测试读写锁
    @ResponseBody
    @GetMapping("/read")
    public String readValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = lock.readLock();
        rLock.lock();
        try {
            s = redisTemplate.opsForValue().get("writeValue");
            System.out.println("未解锁" + Thread.currentThread().getName());
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("已解锁" + Thread.currentThread().getName());
        }
        return s;
    }


    //    信号量测试
//    车位停车 redis存放数据 key=park  value=3
    @GetMapping("/park")   // 当没有车位的时候 value=0 阻塞等待
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire();
        return "ok";
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();
        return "ok";
    }


    //   闭锁测试
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lcokDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await(); //等待闭锁都完成
        return "放假了...";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String lcokDoor(@PathVariable("id") Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown(); //计数减一
        return id + "班人都走了...";
    }
}

