package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 *
 * @author ****
 * @email none
 * @date 2021-12-16 17:56:30
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

}