package com.wjl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gmall.product.model.entity.SkuImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public interface SkuImageMapper extends BaseMapper<SkuImage> {
    /**
     *  根据skuId批量获取
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuImages(@Param("skuId") Long skuId);
}
