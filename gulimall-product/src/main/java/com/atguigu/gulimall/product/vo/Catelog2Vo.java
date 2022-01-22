package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {
    private String catelog1Id;  //一级分类id
    private List<Catelog3Vo> catalog3List;  //三级子分类
    private String id;//二级分类id
    private String name;//二级分类name

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo {
        private String catalog2Id;  //二级分类的id(三级分类父id)
        private String id; //三级分类id
        private String name;//三级分类name
    }

}
