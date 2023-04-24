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


    /**
     * 保存spu基本信息以及关联到的一个spu所拥有的所有信息
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);
    /**
     *  获取spu的图片
     * @param spuId
     * @return
     */
    List<SpuImage> spuImageListBySpuId(Long spuId);

    /**
     *  获取到spu的销售属性集合
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrListBySpuId(Long spuId);

    /**
     * 获取指定spu下的一组销售属性以及对应的销售属性值集合
     * 根据指定的skuId为默认选中
     * 按照spu的基本销售属性排序
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    /**
     *  获取spu海报图片
     * @param spuId
     * @return
     */
    List<SpuPoster> findSpuPosterBySpuId(Long spuId);

    /**
     *  根据SkuId获取平台属性和对应的平台属性值
     */
    List<BaseAttrInfo> getAttrListBySku(Long skuId);

    /**
     * 根据spuId 查询当前spu下所有的spu销售属性值组合id与skuId对应的map
     * key 的组装按照spu的基本销售属性值id排序 (根据 -> getSpuSaleAttrListCheckBySku)
     * 3732|3734: 21
     * 3732|3735: 25
     * 3733|3734: 26
     * 3733|3735: 27
     */
    Map getSkuValueIdsMap(Long spuId);
}
