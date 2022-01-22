package com.atguigu.gulimall.sso.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
public class ShowQQEmail {
    @Value("${sso.server.url}")
    public String ssoServerUrl;

    @GetMapping("/qqemail")
    public String employees(Model model, HttpSession session,
                            @RequestParam(value = "token", required = false) String token) {


        if (!StringUtils.isEmpty(token)) {
            //代表登陆了,跳过来的
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://sso.com:8080/userInfo?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser", body);
        }
//        判断是否登录
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client2.com:8082/qqemail";
        } else {
            ArrayList<String> modules = new ArrayList<>();
            modules.add("收件箱");
            modules.add("发件箱");
            modules.add("垃圾箱");
            modules.add("记事本");
            model.addAttribute("modules", modules);
            return "qqemail";
        }
    }


}
