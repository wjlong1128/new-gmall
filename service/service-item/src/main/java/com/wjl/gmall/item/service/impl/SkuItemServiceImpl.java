package com.wjl.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.item.service.SkuItemService;
import com.wjl.gmall.list.client.ListServiceClient;
import com.wjl.gmall.product.client.ProductServiceClient;
import com.wjl.gmall.common.constant.RedisConst;
import com.wjl.gmall.product.client.model.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@Slf4j
@Service
public class SkuItemServiceImpl implements SkuItemService {

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private ListServiceClient listServiceClient;

    /**
     * 获取并且整理商品详情页数据
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getSkuDetailsBySkuId(Long skuId) {
        Map<String, Object> result = new HashMap<>();
        RBloomFilter<Object> skuBloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);

        //if (skuBloomFilter.contains(skuId)) {
        //    return result;
        //}
        log.info("bloomFilter: {}, exist:{}", RedisConst.SKU_BLOOM_FILTER, skuBloomFilter.contains(skuId));

        CompletableFuture<SkuInfo> spkInfoTask = CompletableFuture.supplyAsync(() -> {
            // spuinfo and images
            SkuInfo skuInfo = productServiceClient.getSkuInfoAndImages(skuId).getData();
            result.put("skuInfo", skuInfo);
            return skuInfo;
        }, executor);

        CompletableFuture<Void> spuSaleAttrListCheckTask = spkInfoTask.thenAcceptAsync(spuInfo -> {
            if (spuInfo != null && spuInfo.getSpuId() != null) {
                // 获取商品切换数据
                List<SpuSaleAttr> spuSaleAttrListCheckBySku = productServiceClient.getSpuSaleAttrListCheckBySku(skuId, spuInfo.getSpuId()).getData();
                result.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
            }
        }, executor);

        CompletableFuture<Void> categoryViewTask = spkInfoTask.thenAcceptAsync(spuInfoAndImages -> {
            if (spuInfoAndImages != null && spuInfoAndImages.getSpuId() != null) {
                // 三级分类视图
                BaseCategoryView categoryView = productServiceClient.getCategoryView(spuInfoAndImages.getCategory3Id()).getData();
                result.put("categoryView", categoryView);
            }
        }, executor);

        CompletableFuture<Void> valuesSkuJsonTask = spkInfoTask.thenAcceptAsync(spuInfoAndImages -> {
            if (spuInfoAndImages != null && spuInfoAndImages.getSpuId() != null) {
                Map<String, Object> skuValueIdsMap = productServiceClient.getSkuValueIdsMap(spuInfoAndImages.getSpuId());
                // 转换为json
                result.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));
            }
        }, executor);

        CompletableFuture<Void> posterTask = spkInfoTask.thenAcceptAsync(spuInfoAndImages -> {
            if (spuInfoAndImages != null && spuInfoAndImages.getSpuId() != null) {
                List<SpuPoster> spuPosterBySpuId = productServiceClient.findSpuPosterBySpuId(spuInfoAndImages.getSpuId()).getData();
                result.put("spuPosterList", spuPosterBySpuId);
            }
        }, executor);

        CompletableFuture<Void> priceTask = CompletableFuture.runAsync(() -> {
            // 实时价格
            BigDecimal skuPrice = productServiceClient.getSkuPrice(skuId).getData();
            result.put("price", skuPrice);
        }, executor);


        CompletableFuture<Void> baseAttrInfosTask = CompletableFuture.runAsync(() -> {
            // 平台属性
            List<BaseAttrInfo> attrList = productServiceClient.getAttrList(skuId).getData();

            // 处理数据
            List<HashMap<String, String>> processAttrInfos = attrList.stream().map(baseAttrInfo -> {
                HashMap<String, String> map = new HashMap<>();
                map.put("attrName", baseAttrInfo.getAttrName());
                String valueName = baseAttrInfo.getAttrValueList().get(0).getValueName();
                map.put("attrValue", valueName);
                return map;
            }).collect(Collectors.toList());
            result.put("skuAttrList", processAttrInfos);
        }, executor);

        // 更新商品热度
        CompletableFuture<Void> incrSkuScoreTask = CompletableFuture.runAsync(() -> {
            listServiceClient.incrHotScore(skuId);
        }, executor);

        CompletableFuture.allOf(
                spkInfoTask,
                spuSaleAttrListCheckTask,
                categoryViewTask,
                valuesSkuJsonTask,
                priceTask,
                posterTask,
                baseAttrInfosTask,
                incrSkuScoreTask
        ).join();

        return result;
    }
}
