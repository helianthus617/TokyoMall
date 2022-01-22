package com.atguigu.gulimall.member.service;

import com.atguigu.common.to.SocialUserInfoTo;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UsernameExistException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;

import java.text.ParseException;
import java.util.Map;

/**
 * 会员
 *
 * @author ****
 * @email none
 * @date 2021-12-16 17:56:30
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    void checkPhoneUnique(String email) throws PhoneExistException;

    void checkUsernameUnique(String username) throws UsernameExistException;

    MemberEntity login(SocialUserInfoTo vo) throws ParseException;

    MemberEntity login(MemberLoginVo vo);
}

