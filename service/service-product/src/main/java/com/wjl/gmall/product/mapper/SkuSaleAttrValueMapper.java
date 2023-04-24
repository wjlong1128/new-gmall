package com.wjl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gmall.product.model.entity.SkuSaleAttrValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 根据spuId 查询当前spu下所有的spu销售属性值组合id与skuId对应的map
     * key 的组装按照spu的基本销售属性值id排序 (根据 -> getSpuSaleAttrListCheckBySku)
     * 3732|3734: 21
     * 3732|3735: 25
     * 3733|3734: 26
     * 3733|3735: 27
     */
    List<Map> getSkuValueIdsMap(@Param("spuId") Long spuId);
}
