package com.wjl.gamll.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.wjl.gamll.item.service.SkuItemService;
import com.wjl.gamll.product.client.ProductServiceClient;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@Service
public class SkuItemServiceImpl implements SkuItemService {

    @Autowired
    private ProductServiceClient productServiceClient;

    /**
     *  获取并且整理商品详情页数据
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getSkuDetailsBySkuId(Long skuId) {
        Map<String, Object> result = new HashMap<>();

        // spuinfo and images
        Result<SkuInfo> spuInfo = productServiceClient.getSkuInfoAndImages(skuId);
        result.put("skuInfo", spuInfo.getData());
        // 实时价格
        Result<BigDecimal> price = productServiceClient.getSkuPrice(skuId);
        result.put("price", price.getData());
        // 平台属性
        Result<List<BaseAttrInfo>> attrList = productServiceClient.getAttrList(skuId);
        List<BaseAttrInfo> baseAttrInfos = attrList.getData();
        // 处理数据
        List<HashMap<String, String>> processAttrInfos = baseAttrInfos.stream().map(baseAttrInfo -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("attrName", baseAttrInfo.getAttrName());
            String valueName = baseAttrInfo.getAttrValueList().get(0).getValueName();
            map.put("attrValue", valueName);
            return map;
        }).collect(Collectors.toList());
        result.put("skuAttrList", processAttrInfos);

        SkuInfo spuInfoAndImages = spuInfo.getData();
        if (spuInfoAndImages != null) {
            // 三级分类视图
            Result<BaseCategoryView> categoryView = productServiceClient.getCategoryView(spuInfoAndImages.getCategory3Id());
            result.put("categoryView", categoryView.getData());
            // 获取商品切换数据
            Result<List<SpuSaleAttr>> spuSaleAttrListCheckBySku = productServiceClient.getSpuSaleAttrListCheckBySku(skuId,spuInfoAndImages.getSpuId());
            result.put("spuSaleAttrList", spuSaleAttrListCheckBySku.getData());
            Map<String, Object> skuValueIdsMap = productServiceClient.getSkuValueIdsMap(spuInfoAndImages.getSpuId());
            // 转换为json
            result.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));
            Result<List<SpuPoster>> spuPosterBySpuId = productServiceClient.findSpuPosterBySpuId(spuInfoAndImages.getSpuId());

            result.put("spuPosterList", spuPosterBySpuId.getData());
        }

        return result;
    }
}
