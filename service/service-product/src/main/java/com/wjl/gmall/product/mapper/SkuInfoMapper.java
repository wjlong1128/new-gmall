package com.wjl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gmall.model.product.SkuInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    BigDecimal getPrice(@Param("skuId") Long skuId);
}
