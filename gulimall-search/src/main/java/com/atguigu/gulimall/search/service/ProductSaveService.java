package com.atguigu.gulimall.search.service;

import com.atguigu.common.to.es.SkuEsModel;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
