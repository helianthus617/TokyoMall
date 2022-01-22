package com.atguigu.gulimall.cart.vo;

import lombok.Data;
import lombok.ToString;
import org.springframework.expression.spel.ast.BooleanLiteral;

import java.math.BigDecimal;

@ToString
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private Boolean tempUser = false;
}