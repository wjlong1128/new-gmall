package com.wjl.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.gmall.model.product.SkuInfo;

import java.math.BigDecimal;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public interface SkuService extends IService<SkuInfo> {
    void saveSku(SkuInfo skuInfo);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SkuInfo getSkuInfoAndImages(Long skuId);

    BigDecimal getSkuPrice(Long skuId);
}
