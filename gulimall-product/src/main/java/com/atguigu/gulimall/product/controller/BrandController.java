package com.atguigu.gulimall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.UpdateGroup;
import com.atguigu.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author ****
 * @email none
 * @date 2021-12-16 18:02:09
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId) {

        BrandEntity brand = brandService.getById(brandId);
        return R.ok().put("brand", brand);
    }


    /**
     * 校验未分组
     */
    @RequestMapping("/save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand) {
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 校验未分组
     */
//    @RequestMapping("/save")
//    public R save(@Valid @RequestBody BrandEntity brand) {
//        brandService.save(brand);
//        return R.ok();
//    }

    /**
     * 使用bingdingresult处理
     */
//    @RequestMapping("/save")
//    public R save(@Valid @RequestBody BrandEntity brand, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            HashMap<String, String> map = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(
//                    (item) -> {
//                        String defaultMessage = item.getDefaultMessage();
//                        String field = item.getField();
//                        map.put(field, defaultMessage);
//                    }
//            );
//            return R.error(400, "提交的数据不合法").put("data", map);
//        } else {
//            brandService.save(brand);
//        }
//        return R.ok();
//    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand) {
        brandService.updateDetail(brand);

        return R.ok();
    }


    @RequestMapping("/update/status")
    // @RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
