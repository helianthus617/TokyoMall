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
public class ShowQQZone {

    @Value("${sso.server.url}")
    public String ssoServerUrl;

    @GetMapping("/qqzone")
    public String employees(Model model, HttpSession session,
                            @RequestParam(value = "token", required = false) String token) {

        if (!StringUtils.isEmpty(token)) {
//代表刚登陆过,然后重定向回来
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://sso.com:8080/userInfo?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser", body);
        }
//        判断是否登录
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client1.com:8081/qqzone";
        } else {
            ArrayList<String> modules = new ArrayList<>();
            modules.add("主页");
            modules.add("相册");
            modules.add("留言");
            modules.add("说说");
            modules.add("日志板");

            model.addAttribute("modules", modules);
            return "qqzone";
        }
    }


}
