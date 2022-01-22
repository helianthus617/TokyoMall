package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/*1、导入mybatisplus依赖,gulimall-common 模块已经导入依赖。
        <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.2.0</version>
        </dependency>
  2,配置
       1，配置数据源
            1），导入数据库驱动 gulimall-common 模块已经导入依赖。
            2），在application.yml 配置数据源相关信息
       2，配置mybatis-plus
            1），使用@MapperScan扫描接口
            2），告诉Mybatis-Plus, .sql映射文件在哪个位置
3,JSR303
 1),给Bean添加校验注解: javax.validation.constraints 校验注解
 2),开启校验功能@Valid
  效果:校验错误以后有默认的相应。
 3).给校验的Bean后添加BindingResult，查看具体错误信息
 4),分组校验
    1）,@NotBlank(message="品牌名必须提交",groups={AddGroup.class}）
    2）,save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand)
 5),自定义校验
    1),编写一个自定义校验注解 @ListValue(vals={0,1})
    2),

4,统一的异常处理
@ControllerAdvice
1),编写异常处理类,使用@ControllerAdvice
2),使用@ExceptionHandler标注方法可以处理异常


@NotNull：
不能为null，但可以为empty(""," “,” ") ，一般用在基本数据类型的非空校验上
@NotBlank：
此注解只能作用在接收的String类型上**，注意是只能**，不能为null，而且调用trim()后，长度必须大于0
@NotEmpty：
不能为null，而且长度必须大于0(" “,” ")，一般用在集合类上面
 */
//整合springcache 简化缓存开发
//1.引入依赖
//  spring-boot-starter-cache , spring-boot-starter-data-redis
//2.写配置
//  自动配置了哪些，CacheAutoConfiguration 会导入RedisCacheConfiguration； 自动配置好了缓存管理器 RedisCacheManager
//  配置使用redis作为缓存
//3.测试使用缓存
//    @Cacheable:触发将数据保存到缓存的操作
//    @CacheEvict:触发将数据从缓存删除的操作
//    @CachePut 不影响方法执行更新缓存
//      @Caching 组合以上几种操作
//      @CacheConfig 在类级别共享缓存的配置
//    1)开启缓存功能 @EnableCaching
//
//Spring-Cache的不足
//1),读请求：
//    缓存穿透 查询一个null 数据 解决 缓存空数据 cahe-null-values=true
//    缓存击穿 大量并发进来同时查询一个正好过期的数据 @Cacheable(value = {"category"}, key = "#root.method.name" ,sync = true)    加本地锁方式

//    缓存雪崩 加上过期时间 spring.cahce.redis.time.to.live
//2),写模式：
//    1、读写加锁
//    2, 引入canal 感知到mysql 的更新去更新数据库
//    3，读多写多  直接查询数据库
@EnableRedisHttpSession
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.product.dao")
public class GulimallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }
}