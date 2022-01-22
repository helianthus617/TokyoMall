package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    boolean hasStock = true;

    //        sku基本信息 pms_sku_info
    SkuInfoEntity info;
    //        sku图片信息 pms_sku_images
    List<SkuImagesEntity> images;
    //        spu销售属性组合
    List<SkuItemSaleAttrVo> saleAttrVos;
    //        spu规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

    SpuInfoDescEntity desp;

    SeckillInfoVo seckillInfo;

}
