package com.wjl.gamll.product.client.impl;

import com.wjl.gamll.product.client.ProductServiceClient;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.*;
import feign.hystrix.FallbackFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public class ProductServiceDegradeClientFallbackFactory implements FallbackFactory<ProductServiceClient> {
    @Override
    public ProductServiceClient create(Throwable cause) {
        return new ProductServiceClient() {
            @Override
            public Result<SkuInfo> getSkuInfoAndImages(Long skuId) {
                return null;
            }

            @Override
            public Result<BaseCategoryView> getCategoryView(Long category3Id) {
                return null;
            }

            @Override
            public Result<BigDecimal> getSkuPrice(Long skuId) {
                return null;
            }

            @Override
            public Result<List<SpuSaleAttr>> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
                return null;
            }

            @Override
            public Result<List<SpuPoster>> findSpuPosterBySpuId(Long spuId) {
                return null;
            }

            @Override
            public Result<List<BaseAttrInfo>> getAttrList(Long skuId) {
                return null;
            }

            @Override
            public Map<String, Object> getSkuValueIdsMap(Long spuId) {
                return null;
            }
        };
    }
}
