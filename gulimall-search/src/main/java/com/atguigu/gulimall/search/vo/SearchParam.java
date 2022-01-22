package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    private String keyword;  //页面传递过来的全文匹配关键字
    private Long catalog3Id; //三级分类Id
    //    sort =saleCount_asc/desc
//    sort =skuPrice_asc/desc
//    sort =hotScore_asc/desc
    private String sort;
    //过滤条件
//      hasStock skuPrice brandId catalog3Id attrs
//      hasStock=0/1
//      skuPrice=1_500/_500/500_
    private Integer hasStock;
    private String skuPrice;
    private List<Long> brandId;
    //      attrs=2_5寸:6寸
    private List<String> attrs;
    private String _queryString;//原生的所有请求
    private Integer pageNum = 1;
}
