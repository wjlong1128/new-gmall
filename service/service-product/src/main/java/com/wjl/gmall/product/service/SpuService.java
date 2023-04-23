package com.wjl.gmall.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gmall.model.product.*;

import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public interface SpuService {
    Page<SpuInfo> getSpuInfoList(Long page, Long limit, SpuInfo spuInfo);

    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuImage> spuImageListBySpuId(Long spuId);

    List<SpuSaleAttr> spuSaleAttrListBySpuId(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    List<SpuPoster> findSpuPosterBySpuId(Long spuId);

    List<BaseAttrInfo> getAttrListBySku(Long skuId);

    Map getSkuValueIdsMap(Long spuId);
}
