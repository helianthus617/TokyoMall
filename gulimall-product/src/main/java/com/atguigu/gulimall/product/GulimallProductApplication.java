package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


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
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.product.dao")
public class GulimallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }
}