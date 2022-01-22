package com.atguigu.gulimall.auth.vo;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@ToString
@Data
public class UserRegisterVo {
    @Length(min = 6, max = 18, message = "用户名必须是6-18位字符")
    private String userName;
    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    private String password;
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;
    private String code;
}