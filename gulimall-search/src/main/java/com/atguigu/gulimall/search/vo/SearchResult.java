package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResult {
    private List<SkuEsModel> products;
    private Integer totalPages; // 总页码
    private Long total; //总记录数
    private Integer pageNum;
    private List<BrandVo> brands;  //查询到的结果所涉及的品牌
    private List<CatalogVo> catalogs;//查询到的结果所涉及的所有分类
    private List<AttrVo> attrs;//查询到的结果所涉及的所有属性
    private List<Integer> pageNavs;
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
