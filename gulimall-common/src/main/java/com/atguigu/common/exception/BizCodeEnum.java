package com.atguigu.common.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验异常"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，请稍后再试"),
    USER_EXISTS_EXCEPTION(15001, "用户名已存在"),
    PHONE_EXISTS_EXCEPTION(15002, "手机号已存在"),
    PRODUCT_UP_EXCEPTION(10002, "商品上架异常"),
    LOGIN_ACCOUNT_PASSWORD_INVALID(15003, "用户名或密码错误"),
    LOGIN_AUTHORIZATION_FAIL(15004, "AUTH2.0认证登录失败"),
    NO_STOCK_EXCEPTION(21000, "商品库存不足"),
    TOO_MANY_REQUEST(10005, "服务器资源忙toCentinel");
    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
