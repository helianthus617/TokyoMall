package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.MemberRespVo;
import com.atguigu.common.to.mq.SeckillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionsWithSkus;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";

    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";

    private final String SKUSTOCK_SEMAPHONE = "seckill:stock:";

    @Override
    public void uploadSeckillSkuLatest3Day() {
        // 1.扫描最近三天要参加秒杀的商品，
        // 注意!  往redis里面存的时候时间戳多了八小时
        R r = couponFeignService.getLate3DaySession();
        if (r.getCode() == 0) {
            List<SeckillSessionsWithSkus> sessions = r.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            // 2.缓存活动信息
            saveSessionInfo(sessions);
            // 3.缓存活动的关联的商品信息
            saveSessionSkuInfo(sessions);
        }
    }

    public List<SeckillSkuRedisTo> blockHandler(BlockException e) {
        log.error("getCurrentSeckillSkusResource被限流了,{}", e.getMessage());
        return null;
    }

    @SentinelResource(value = "getCurrentSeckillSkusResource", blockHandler = "blockHandler")
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        try (Entry entry = SphU.entry("seckillSkus")) {
            // 1.确定当前时间属于那个秒杀场次
            long time = new Date().getTime();

            Set<String> keys = stringRedisTemplate.keys(SESSION_CACHE_PREFIX + "*");
            for (String key : keys) {

                String replace = key.replace("seckill:sessions:", "");
                String[] split = replace.split("_");
                long start = Long.parseLong(split[0]);
                long end = Long.parseLong(split[1]);
                if (time >= start && time <= end) {
                    // 2.获取这个秒杀场次的所有商品信息
                    List<String> range = stringRedisTemplate.opsForList().range(key, 0, 100);
                    BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = hashOps.multiGet(range);
                    if (list != null) {
                        return list.stream().map(item -> {
                            SeckillSkuRedisTo redisTo = JSON.parseObject(item, SeckillSkuRedisTo.class);
//						redisTo.setRandomCode(null);
                            return redisTo;
                        }).collect(Collectors.toList());
                    }
                    break;
                }
            }
        } catch (BlockException e) {
            log.error("资源被限流{}", e.getMessage());
        }
        return null;
    }

    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            String regx = "\\d-" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo to = JSON.parseObject(json, SeckillSkuRedisTo.class);
                    //  查出来的时间少八小时需要在此处添加,
                    long current = new Date().getTime();
                    // 处理一下随机码
                    //存储的对象时间八小时
                    to.setEndTime(to.getEndTime() - 28800000L);
                    to.setStartTime(to.getStartTime() - 28800000L);
                    //业务数据情况如下。 还未开始，还未结束
                    if (current <= to.getStartTime()) {
                        to.setRandomCode(null);
                        return to;
                    }
//                    已开始，还未结束
                    if (current > to.getStartTime()) {
                        return to;
                    }
                }
            }
        }
        return null;

    }

    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {


        long s1 = System.currentTimeMillis();
        //获取当前用户的信息
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        //1、获取当前秒杀商品的详细信息从Redis中获取
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String skuInfoValue = hashOps.get(killId);
        if (StringUtils.isEmpty(skuInfoValue)) {
            return null;
        }
        //(合法性效验)
        SeckillSkuRedisTo redisTo = JSON.parseObject(skuInfoValue, SeckillSkuRedisTo.class);
        Long startTime = redisTo.getStartTime();
        Long endTime = redisTo.getEndTime();
        long currentTime = System.currentTimeMillis();
        //判断当前这个秒杀请求是否在活动时间区间内(效验时间的合法性)
        if (currentTime >= startTime && currentTime <= endTime) {

            //2、效验随机码和商品id
            String randomCode = redisTo.getRandomCode();
            String skuId = redisTo.getPromotionSessionId() + "-" + redisTo.getSkuId();
            if (randomCode.equals(key) && killId.equals(skuId)) {
                //3、验证购物数量是否合理和库存量是否充足
                Integer seckillLimit = redisTo.getSeckillLimit().intValue();

                //获取信号量
                String seckillCount = stringRedisTemplate.opsForValue().get(SKUSTOCK_SEMAPHONE + randomCode);
                Integer count = Integer.valueOf(seckillCount);
                //判断信号量是否大于0,并且买的数量不能超过库存
                if (count > 0 && num <= seckillLimit && count > num) {
                    //4、验证这个人是否已经买过了（幂等性处理）,如果秒杀成功，就去占位。userId-sessionId-skuId
                    //SETNX 原子性处理
                    String redisKey = memberRespVo.getId() + "-" + skuId;
                    //设置自动过期(活动结束时间-当前时间)
                    Long ttl = endTime - currentTime;
                    Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                    if (aBoolean) {
                        //占位成功说明从来没有买过,分布式锁(获取信号量-1)
                        RSemaphore semaphore = redissonClient.getSemaphore(SKUSTOCK_SEMAPHONE + randomCode);
                        //TODO 秒杀成功，快速下单
                        boolean semaphoreCount = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                        //保证Redis中还有商品库存
                        if (semaphoreCount) {
                            //创建订单号和订单信息发送给MQ
                            // 秒杀成功 快速下单 发送消息到 MQ 整个操作时间在 10ms 左右
                            String timeId = IdWorker.getTimeId();
                            SeckillOrderTo orderTo = new SeckillOrderTo();
                            orderTo.setOrderSn(timeId);
                            orderTo.setMemberId(memberRespVo.getId());
                            orderTo.setNum(num);
                            orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                            orderTo.setSkuId(redisTo.getSkuId());
                            orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", orderTo);
                            long s2 = System.currentTimeMillis();
                            log.info("耗时..." + (s2 - s1));
                            return timeId;
                        }
                    }
                }
            }
        }
        long s3 = System.currentTimeMillis();
        log.info("耗时..." + (s3 - s1));
        return null;
    }


    private void saveSessionSkuInfo(List<SeckillSessionsWithSkus> sessions) {
        if (sessions != null) {
            sessions.stream().forEach(session -> {
                BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                    // 1.商品的随机码
                    String randomCode = UUID.randomUUID().toString().replace("-", "");
                    if (!ops.hasKey(seckillSkuVo.getPromotionSessionId() + "-" + seckillSkuVo.getSkuId())) {
                        // 2.缓存商品
                        SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                        BeanUtils.copyProperties(seckillSkuVo, redisTo);
                        // 3.sku的基本数据 sku的秒杀信息
                        R info = productFeignService.skuInfo(seckillSkuVo.getSkuId());
                        if (info.getCode() == 0) {
                            SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                            });
                            redisTo.setSkuInfoVo(skuInfo);
                        }
                        // 4.设置当前商品的秒杀信息
                        redisTo.setStartTime(session.getStartTime().getTime() - 28800000L);
                        redisTo.setEndTime(session.getEndTime().getTime() - 28800000L);
                        redisTo.setRandomCode(randomCode);
//		key SKUKILL_CACHE_PREFIX   value-key 2021：12：01：0：00：00- 2021：12：01：0：00：00    value-value 场次3  sku=1，2，3
                        ops.put(seckillSkuVo.getPromotionSessionId() + "-" + seckillSkuVo.getSkuId(), JSON.toJSONString(redisTo));
                        // 如果当前这个场次的商品库存已经上架就不需要上架了
                        // 5.使用库存作为分布式信号量  限流
                        RSemaphore semaphore = redissonClient.getSemaphore(SKUSTOCK_SEMAPHONE + randomCode);
                        semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
                    }
                });
            });
        }
    }

    private void saveSessionInfo(List<SeckillSessionsWithSkus> sessions) {
        if (sessions != null) {
            sessions.stream().forEach(session -> {
                long startTime = session.getStartTime().getTime() - 28800000L;
                long endTime = session.getEndTime().getTime() - 28800000L;

                String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
                Boolean hasKey = stringRedisTemplate.hasKey(key);
                if (!hasKey) {
                    // 获取场次id_skuid
                    List<String> collect = session.getRelationSkus().stream().map
                            (item -> item.getPromotionSessionId() + "-" + item.getSkuId()).collect(Collectors.toList());
                    // 缓存活动信息
                    stringRedisTemplate.opsForList().leftPushAll(key, collect);
                }
            });
        }
    }


}
