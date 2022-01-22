package com.atguigu.gulimall.member.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.to.MemberRespVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 这个请求直接放行
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if (match) {
            return true;
        }
        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.SESSION_LOGIN_KEY);
        if (attribute != null) {
            loginUser.set(attribute);
            return true;
        } else {
            request.getSession().setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }

}
