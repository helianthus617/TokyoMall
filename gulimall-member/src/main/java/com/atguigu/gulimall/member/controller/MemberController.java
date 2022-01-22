package com.atguigu.gulimall.member.controller;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.SocialUserInfoTo;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UsernameExistException;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 会员
 *
 * @author ****
 * @email none
 * @date 2021-12-16 17:56:30
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @Autowired
    CouponFeignService couponFeignService;

    @RequestMapping("/coupons")
    public R test() {
        R membercoupons = couponFeignService.membercoupons();
        return R.ok().put("coupon", membercoupons.get("coupons"));
    }


    @PostMapping("/oauth2/login")
    public R login(@RequestBody SocialUserInfoTo vo) {

        try {
            MemberEntity login = memberService.login(vo);
            if (login != null) {
                return R.ok().setData(login);
            } else {
                return R.error(BizCodeEnum.LOGIN_AUTHORIZATION_FAIL.getCode(), BizCodeEnum.LOGIN_AUTHORIZATION_FAIL.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(BizCodeEnum.LOGIN_AUTHORIZATION_FAIL.getCode(), BizCodeEnum.LOGIN_AUTHORIZATION_FAIL.getMsg());
        }

    }


    @PostMapping("login")
    public R login(@RequestBody MemberLoginVo vo) {
        MemberEntity memberEntity = memberService.login(vo);
        if (memberEntity != null) {
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getCode(),
                    BizCodeEnum.LOGIN_ACCOUNT_PASSWORD_INVALID.getMsg());
        }
    }


    @PostMapping("/regist")
    // @RequiresPermissions("member:member:list")
    public R regist(@RequestBody MemberRegistVo vo) {
        try {
            memberService.regist(vo);
        } catch (PhoneExistException e) {
            System.out.println(e.getMessage());
            return R.error(BizCodeEnum.PHONE_EXISTS_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXISTS_EXCEPTION.getMsg());
        } catch (UsernameExistException e) {
            System.out.println(e.getMessage());
            return R.error(BizCodeEnum.USER_EXISTS_EXCEPTION.getCode(), BizCodeEnum.USER_EXISTS_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);
        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
