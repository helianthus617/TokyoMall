package com.atguigu.common.to;

import com.atguigu.common.auth.GiteeAccessToken;
import com.atguigu.common.auth.GiteeUserInfo;
import lombok.Data;

@Data
public class SocialUserInfoTo {
    GiteeUserInfo giteeUserInfo;
    GiteeAccessToken giteeAccessToken;
}
