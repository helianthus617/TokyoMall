package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.MemberRespVo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartyFeignService;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Controller
public class LoginController {
    @Autowired
    private ThirdPartyFeignService feignService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {

        //1、接口防刷
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotBlank(redisCode)) {
            //redis存的时间戳
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                //1分钟内已给这个手机号发过短信，不能在发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        //2、验证码再次校验，存redis ,key=phone,value=code
        Random random = new Random();
        int randomNum = random.nextInt(1000000);
        String code = String.valueOf(randomNum);
        //redis中存储的验证码+时间戳
        String redisValue = code + "_" + System.currentTimeMillis();
        //redis缓存验证码，防止同一个手机号再次发送验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, redisValue, 10, TimeUnit.MINUTES);
        R r = feignService.sendCode(phone, code);
        System.out.println(r);
        return R.ok();
    }

    //    重定向携带数据利用session 原理,只要跳到下个页面取到数据后，session 就会删掉数据
    //    RedirectAttributes redirectAttributes 模拟重定向携带数据
    @PostMapping("/regist")
    public String register(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //校验出错，转发到注册页
            Map<String, String> map = result.getFieldErrors().stream().
                    collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", map);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //真正注册，调用远程服务注册
        //1、校验验证码
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (StringUtils.isNotBlank(s)) {
            if (code.equals(s.split("_")[0])) {
                //删除验证码;令牌机制
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //验证码校验通过,真正注册，调用远程服务注册
                R regist = memberFeignService.regist(vo);

                if (regist.getCode() == 0) {
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    Map<String, Object> errors = new HashMap<>();
                    errors.put("msg", regist.getData("msg", new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("code", "验证码对比后输入错误");
                redirectAttributes.addFlashAttribute("errors", map);
                //校验出错，转发到注册页
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("code", "验证码输入错误,可能未发送验证码");
            redirectAttributes.addFlashAttribute("errors", map);
            //校验出错，转发到注册页
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }


    @GetMapping(value = "/loguot.html")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(AuthServerConstant.SESSION_LOGIN_KEY);
        request.getSession().invalidate();
        return "redirect:http://gulimall.com";
    }


    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
        R r = memberFeignService.login(vo);
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
            Map<String, Object> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }
}