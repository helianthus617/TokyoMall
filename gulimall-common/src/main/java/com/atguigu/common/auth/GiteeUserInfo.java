package com.atguigu.common.auth;

import lombok.Data;

@Data
public class GiteeUserInfo {

    private long id;

    private String login;

    private String name;

    private String avatar_url;

    private String url;

    private String html_url;

    private String followers_url;

    private String following_url;

    private String gists_url;

    private String starred_url;

    private String subscriptions_url;

    private String organizations_url;

    private String repos_url;

    private String events_url;

    private String received_events_url;

    private String type;

    private String blog;

    private String weibo;

    private String bio;

    private long public_repos;

    private long public_gists;

    private long followers;

    private long following;

    private long stared;

    private long watched;

    private String created_at;

    private String updated_at;

    private String email;

}