package com.wjl.gamll.item.service;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public interface SkuItemService {
    /**
     *  获取商品详情页内容
     * @param skuId
     * @return
     */
    Map<String,Object> getSkuDetailsBySkuId(Long skuId);
}
