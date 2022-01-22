package com.atguigu.gulimall.member.web;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberWebController {
    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping(value = "/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", required = false, defaultValue = "0")
                                          Integer pageNum,
                                  Model model
    ) {
        Map<String, Object> page = new HashMap<>();
        page.put("page", pageNum.toString());
        R r = orderFeignService.listWithItem(page);
        model.addAttribute("orders", r);
        return "orderList";
    }
}