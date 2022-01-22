package com.atguigu.gulimall.member.service.impl;

import com.atguigu.common.to.SocialUserInfoTo;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UsernameExistException;
import com.atguigu.gulimall.member.vo.MemberLoginVo;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity entity = new MemberEntity();
        MemberDao baseMapper = this.baseMapper;
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefalutLevel();
        entity.setLevelId(memberLevelEntity.getId());
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUserName());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);
        entity.setMobile(vo.getPhone());
        entity.setUsername(vo.getUserName());
        baseMapper.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        MemberDao baseMapper = this.baseMapper;
        Integer phone1 = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (phone1 > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        MemberDao baseMapper = this.baseMapper;
        Integer username1 = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (username1 > 0) {
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(SocialUserInfoTo vo) throws ParseException {
        long uid = vo.getGiteeUserInfo().getId();
        MemberDao baseMapper = this.baseMapper;
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (memberEntity != null) {
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(vo.getGiteeAccessToken().getAccess_token());
            update.setExpiresIn(String.valueOf(vo.getGiteeAccessToken().getExpires_in()));
            baseMapper.updateById(update);
            memberEntity.setAccessToken(vo.getGiteeAccessToken().getAccess_token());
            memberEntity.setExpiresIn(String.valueOf(vo.getGiteeAccessToken().getExpires_in()));
            return memberEntity;
        }
        {
            MemberEntity member = new MemberEntity();
            member.setSocialUid(String.valueOf(vo.getGiteeUserInfo().getId()));
            member.setAccessToken(vo.getGiteeAccessToken().getAccess_token());
            member.setExpiresIn(String.valueOf(vo.getGiteeAccessToken().getExpires_in()));
            member.setNickname(vo.getGiteeUserInfo().getName());
            String created_at = vo.getGiteeUserInfo().getCreated_at().substring(0, 10);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parse = simpleDateFormat.parse(created_at);
            member.setCreateTime(parse);
            member.setNickname(vo.getGiteeUserInfo().getLogin());
            member.setUsername(vo.getGiteeUserInfo().getLogin());
            baseMapper.insert(member);
            return member;
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberDao baseMapper = this.baseMapper;
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().
                eq("username", loginacct).or().eq("mobile", loginacct));
        if (memberEntity == null) {
            return null;
        } else {
            String passwordb = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, passwordb);
            if (matches) {
                return memberEntity;
            } else {
                return null;
            }

        }

    }
}