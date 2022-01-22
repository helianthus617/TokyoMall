package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.to.MemberRespVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;


public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
//    第一次打开未登录 userInfoTo.Userkey 不为空 userid为空  保存cookie 1个月
//    第二次打开未登录 userInfoTo.Userkey 不为空 userid为空  不更新cookie
//    第三次打开登录  userInfoTo .Userkey 不为空 userid不为空  不更新cookie

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//      登录了就有用户的id，没有登录就只有用户的key
        UserInfoTo userInfoTo = new UserInfoTo();
//        getSession()相当于getSession(true);
//        参数为true时，若存在会话，则返回该会话，否则新建一个会话；
//        参数为false时，如存在会话，则返回该会话，否则返回NULL；
//处理debug,如果使用 request.getSession(); 的方式获得session ，如果未登录，直接打开购物车页面，页面会自动创建session，此时是没有必要的
        HttpSession session = request.getSession(false);
        MemberRespVo member;
        if (session == null) {
            member = null;
        } else {
            member = (MemberRespVo) session.getAttribute(AuthServerConstant.SESSION_LOGIN_KEY);
        }

        if (member != null) {
//      用户是登录的
            userInfoTo.setUserId(member.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        //如果没有临时用户一定分配个临时用户,新装的电脑第一次打开
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        threadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        //getTempUser()默认是false
        if (!userInfoTo.getTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
