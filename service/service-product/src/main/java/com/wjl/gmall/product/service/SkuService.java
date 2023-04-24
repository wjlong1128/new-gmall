package com.wjl.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.gmall.product.model.entity.SkuInfo;

import java.math.BigDecimal;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public interface SkuService extends IService<SkuInfo> {

    /**
     *  保存一个sku（商品）信息及其所关联的信息
     * @param skuInfo
     * @return
     */
    void saveSku(SkuInfo skuInfo);

    /**
     * 上架
     * @param skuId
     */
    void onSale(Long skuId);

    /**
     *  下架
     * @param skuId
     */
    void cancelSale(Long skuId);

    /**
     *  根据sku获取所属sku的基本信息以及sku图片信息
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfoAndImages(Long skuId);

    /**
     *  获取sku实时价格
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(Long skuId);
}
