package com.wjl.gmall.item.api;

import com.wjl.gmall.item.service.SkuItemService;
import com.wjl.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@RequestMapping("api/item")
@RestController
public class SpuItemController {

    @Autowired
    private SkuItemService spuItemService;

    /**
     *  获取商品详情页数据
     * @param skuId
     * @return
     */
    @GetMapping("{skuId}")
    public Result<Map<String, Object>> getSpuItemDetail(@PathVariable("skuId") Long skuId){
        Map<String, Object> details = spuItemService.getSkuDetailsBySkuId(skuId);
        return Result.ok(details);
    }

}
