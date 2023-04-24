package com.wjl.gmall.product.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.SkuInfo;
import com.wjl.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@RestController
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     *  保存一个sku（商品）信息及其所关联的信息
     * @param skuInfo
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        skuService.saveSku(skuInfo);
        return Result.ok();
    }


    @GetMapping("/list/{page}/{limit}")
    public Result<IPage<SkuInfo>> spuInfos(
            @PathVariable("page") Long page,
            @PathVariable("limit") Long limit
    ) {
        Page<SkuInfo> infoPage = skuService.page(new Page<>(page, limit));
        return Result.ok(infoPage);
    }

    /**
     * 商品上架
     *
     * @return
     */
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId) {
        skuService.onSale(skuId);
        return Result.ok();
    }

    /**
     *  商品下架
     * @param skuId
     * @return
     */
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId) {
        skuService.cancelSale(skuId);
        return Result.ok();
    }
}
