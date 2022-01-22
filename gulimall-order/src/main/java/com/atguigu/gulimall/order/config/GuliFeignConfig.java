package com.atguigu.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class GuliFeignConfig {
    //    远程调用cart服务,请求先到达cart服务的拦截器preHandle，
//    拦截器根据session对象获得登录信息，但是默认远程调用会丢掉这部分信息（请求头信息）
//    所以拦截器获得不了session信息
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override//设置 feign调用时包含请求头
            public void apply(RequestTemplate requestTemplate) {
                //获得老请求的内容
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    //添加进新请求
                    if (request != null) {
                        //同步信息
                        String cookie = request.getHeader("Cookie");
                        // user-key , GULISESSION
//                      给新请求提供老请求的请求头数据
                        requestTemplate.header("Cookie", cookie);
                    }
                }
            }
        };
    }
}
