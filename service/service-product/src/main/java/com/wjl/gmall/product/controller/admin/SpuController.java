package com.wjl.gmall.product.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.SpuImage;
import com.wjl.gmall.model.product.SpuInfo;
import com.wjl.gmall.model.product.SpuSaleAttr;
import com.wjl.gmall.product.service.SpuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@RequestMapping("admin/product")
@RestController
@RequiredArgsConstructor
public class SpuController {


    private final SpuService spuService;

    @GetMapping("{page}/{limit}")
    public Result<Page<SpuInfo>> getSpuInfoList(
            @PathVariable Long page,
            @PathVariable Long limit,
            SpuInfo spuInfo
    ) {
        Page<SpuInfo> spuInfoPage = spuService.getSpuInfoList(page, limit, spuInfo);
        return Result.ok(spuInfoPage);
    }


    /**
     * 保存spu信息
     * @param spuInfo
     * @return
     */
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuService.saveSpuInfo(spuInfo);
        return  Result.ok();
    }

    /**
     *  获取spu图片
     * @param spuId
     * @return
     */
    @GetMapping("/spuImageList/{spuId}")
    private Result<List<SpuImage>> spuImageList(@PathVariable Long spuId){
        List<SpuImage> spuImages =  spuService.spuImageListBySpuId(spuId);
        return Result.ok(spuImages);
    }

    /**
     *  获取spu的销售属性集合
     * @param spuId
     * @return
     */
    @GetMapping("spuSaleAttrList/{spuId}")
    private Result<List<SpuSaleAttr>> spuSaleAttrList(@PathVariable Long spuId){
        List<SpuSaleAttr> spuSaleAttrList =  spuService.spuSaleAttrListBySpuId(spuId);
        return Result.ok(spuSaleAttrList);
    }


}
