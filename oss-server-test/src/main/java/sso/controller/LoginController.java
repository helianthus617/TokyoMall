package sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Controller
public class LoginController {
    @Autowired
    StringRedisTemplate redisTemplate;


    @ResponseBody
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("token") String token) {
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }


    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String url,
                            Model model,
                            @CookieValue(value = "sso_token", required = false) String sso_token) {
        if (!StringUtils.isEmpty(sso_token)) {
            //说明之前有人登录过,这次不用登录
            return "redirect:" + url + "?token=" + sso_token;
        }
        model.addAttribute("url", url);
        return "login";
    }

    //    登陆成功后需要跳回原登录页面.
    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("url") String url,
                          HttpServletResponse httpServletResponse
    ) {
//        查询数据库
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
//        将登陆成功的用户添加到redis中
            String uuid = UUID.randomUUID().toString().replace("_", "");
            redisTemplate.opsForValue().set(uuid, username);
//        保留cookie信息(重点)
            Cookie sso_token = new Cookie("sso_token", uuid);
            httpServletResponse.addCookie(sso_token);
            return "redirect:" + url + "?token=" + uuid;
        }
        return "login";
    }
}
