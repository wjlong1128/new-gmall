package com.wjl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gmall.model.product.SkuSaleAttrValue;
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
    List<Map> getSkuValueIdsMap(@Param("spuId") Long spuId);
}
