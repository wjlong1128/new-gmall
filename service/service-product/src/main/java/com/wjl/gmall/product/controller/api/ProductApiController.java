package com.wjl.gmall.product.controller.api;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.product.model.entity.*;
import com.wjl.gmall.product.model.vo.CategoryVO;
import com.wjl.gmall.product.service.BaseManagerService;
import com.wjl.gmall.product.service.SkuService;
import com.wjl.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@RequestMapping("/api/product/inner")
@RestController
public class ProductApiController {

    @Autowired
    private SkuService skuService;

    @Autowired
    private BaseManagerService baseManagerService;

    @Autowired
    private SpuService spuService;


    /**
     *  获取spu基本信息和图片集合
     * @param skuId
     * @return
     */
    @GetMapping("getSkuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfoAndImages(@PathVariable("skuId") Long skuId) {
        SkuInfo info = skuService.getSkuInfoAndImages(skuId);
        return Result.ok(info);
    }

    /**
     *  查询分类视图  手机 > 手机通讯 > 智能手机
     * @param category3Id
     * @return
     */
    @GetMapping("getCategoryView/{category3Id}")
    public Result<BaseCategoryView> getCategoryView(@PathVariable("category3Id") Long category3Id) {
        BaseCategoryView view = baseManagerService.getCategoryView(category3Id);
        return Result.ok(view);
    }


    /**
     * 获取最新价格
     * @param skuId
     * @return
     */
    @GetMapping("getSkuPrice/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price =  skuService.getSkuPrice(skuId);
        return Result.ok(price);
    }


    /**
     * 获取指定spu下的一组销售属性以及对应的销售属性值集合
     * 根据指定的skuId为默认选中
     * 按照spu的基本销售属性排序
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrListCheckBySku(
            @PathVariable("skuId") Long skuId,
            @PathVariable("spuId") Long spuId
    ){
        List<SpuSaleAttr> spuSaleAttrValues = spuService.getSpuSaleAttrListCheckBySku(skuId,spuId);
        return Result.ok(spuSaleAttrValues);
    }

    /**
     *  根据spu获取海报信息
     */
    @GetMapping("/findSpuPosterBySpuId/{spuId}")
    public Result<List<SpuPoster>> findSpuPosterBySpuId(@PathVariable("spuId") Long spuId){
        List<SpuPoster> posterList = spuService.findSpuPosterBySpuId(spuId);
        return Result.ok(posterList);
    }

    /**
     *  根据SkuId获取平台属性和对应的平台属性值
     */
    @GetMapping("/getAttrList/{skuId}")
    public Result<List<BaseAttrInfo>> getAttrList(@PathVariable("skuId") Long skuId){
        List<BaseAttrInfo> attrInfos = spuService.getAttrListBySku(skuId);
        return Result.ok(attrInfos);
    }


    /**
     * 根据spuId 查询当前spu下所有的spu销售属性值组合id与skuId对应的map
     * key 的组装按照spu的基本销售属性值id排序 (根据 -> getSpuSaleAttrListCheckBySku)
     * 3732|3734: 21
     * 3732|3735: 25
     * 3733|3734: 26
     * 3733|3735: 27
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        return spuService.getSkuValueIdsMap(spuId);
    }


    @GetMapping("getBaseCategoryList")
    public Result<List<CategoryVO>> getBaseCategoryList(){
        List<CategoryVO> categoryList = baseManagerService.getCategoryList();
        return Result.ok(categoryList);
    }
}
