package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.common.to.SocialUserInfoTo;
import com.atguigu.common.auth.GiteeAccessToken;
import com.atguigu.common.auth.GiteeUserInfo;
import com.atguigu.common.to.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class OAuth2Controller {
    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/gitee/success")
    public String gitee(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {
        Map<String, String> bodys = new HashMap<>();
        bodys.put("grant_type", "authorization_code");
        bodys.put("code", code);
        bodys.put("client_id", "eef201861eaa551caaae07c04453c17b932eeeaf86a10c32dc718f13e08a8e29");
        bodys.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/gitee/success");
        bodys.put("client_secret", "d761340c7a228bcd62297527fcf6156798905d21be50b1d8968fac2874751d4c");
        //1.根据code换取GiteeAccessToken;
        HttpResponse response_GiteeAccesstoken = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", new HashMap(), new HashMap(), bodys);

        //处理返回值
        if (response_GiteeAccesstoken.getStatusLine().getStatusCode() == 200) {
            //获取到了GiteeAccessToken
            System.out.println(response_GiteeAccesstoken.getEntity());
            String tokenJson = EntityUtils.toString(response_GiteeAccesstoken.getEntity());
            GiteeAccessToken token = JSON.parseObject(tokenJson, GiteeAccessToken.class);
            //根据Token，通过查询Gitee Open Api获取用户信息
            Map<String, String> query = new HashMap<>();
            query.put("access_token", token.getAccess_token());
            HttpResponse response_userInfo = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap(), query);
            String giteeUserInfoJson = EntityUtils.toString(response_userInfo.getEntity());
            GiteeUserInfo giteeUserInfo = JSON.parseObject(giteeUserInfoJson, GiteeUserInfo.class);
            //传送过期时间
            SocialUserInfoTo socialUserInfoTo = new SocialUserInfoTo();
            socialUserInfoTo.setGiteeUserInfo(giteeUserInfo);
            socialUserInfoTo.setGiteeAccessToken(token);

            R r = memberFeignService.loginWithAuth(socialUserInfoTo);
            if (r.getCode() == 0) {
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登录信息" + data);
                session.setAttribute(AuthServerConstant.SESSION_LOGIN_KEY, data);
//                String id = session.getId();
//                Cookie jsession = new Cookie("JSESSIONID", id); //默认时间是会话
//                jsession.setDomain("gulimall.com");
//                servletResponse.addCookie(jsession);
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }

        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }
}