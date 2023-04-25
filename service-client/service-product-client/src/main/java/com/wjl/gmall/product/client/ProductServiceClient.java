package com.wjl.gmall.product.client;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.product.client.impl.ProductServiceDegradeClientFallbackFactory;
import com.wjl.gmall.product.client.model.dto.*;
import com.wjl.gmall.product.client.model.vo.CategoryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@FeignClient(value = "service-product", fallbackFactory = ProductServiceDegradeClientFallbackFactory.class)
public interface ProductServiceClient {

    /**
     * 获取spu基本信息和图片集合
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfoAndImages(@PathVariable("skuId") Long skuId);

    /**
     * 查询分类视图  手机 > 手机通讯 > 智能手机
     *
     * @param category3Id
     * @return
     */
    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    public Result<BaseCategoryView> getCategoryView(@PathVariable("category3Id") Long category3Id);


    /**
     * 获取最新价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable("skuId") Long skuId);

    /**
     * 获取指定spu下的一组销售属性以及对应的销售属性值集合
     * 根据指定的skuId为默认选中
     * 按照spu的基本销售属性排序
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrListCheckBySku(
            @PathVariable("skuId") Long skuId,
            @PathVariable("spuId") Long spuId
    );

    /**
     * 根据spu获取海报信息
     */
    @GetMapping("/api/product/inner/findSpuPosterBySpuId/{spuId}")
    public Result<List<SpuPoster>> findSpuPosterBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据SkuId获取具体的平台属性和对应的平台属性值
     */
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    public Result<List<BaseAttrInfo>> getAttrList(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuId 查询当前spu下所有的spu销售属性值组合id与skuId对应的map
     * key 的组装按照spu的基本销售属性值id排序 (根据 -> getSpuSaleAttrListCheckBySku)
     * 3732|3734: 21
     * 3732|3735: 25
     * 3733|3734: 26
     * 3733|3735: 27
     *
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId);


    /**
     * 获取所有分类集合
     *
     * @return
     */
    @GetMapping("/api/product/inner/getBaseCategoryList")
    public Result<List<CategoryVO>> getBaseCategoryList();


    /**
     * 获取品牌对象
     *
     * @param tmId
     * @return
     */
    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    public Result<BaseTrademark> getTrademark(@PathVariable("tmId") Long tmId);
}
