package com.atguigu.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoStockException extends RuntimeException {
    private Long skuId;

    public NoStockException(Long skuId) {
        super("商品id：" + skuId + "库存不足！");
    }

    public NoStockException(String msg) {
        super(msg);
    }

}