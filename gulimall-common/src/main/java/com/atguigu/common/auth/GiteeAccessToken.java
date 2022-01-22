package com.atguigu.common.auth;

import lombok.Data;

@Data
public class GiteeAccessToken {

    private String access_token;
    private String token_type;
    private long expires_in;
    private String refresh_token;
    private String scope;
    private long created_at;

}