package com.wjl.gamll.product.client;

import com.wjl.gamll.product.client.impl.ProductServiceDegradeClientFallbackFactory;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.*;
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
     * 查询分类视图
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
     * 查询spu销售属性，销售属性值，和sku选中的属性状态
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
     * 根据skuid获取平台属性和平台属性值
     */
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    public Result<List<BaseAttrInfo>> getAttrList(@PathVariable("skuId") Long skuId);


    /**
     * 根据spuId 查询map 集合属性
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    Map<String, Object> getSkuValueIdsMap(@PathVariable("spuId") Long spuId);
}
