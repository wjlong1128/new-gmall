package com.wjl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gmall.product.model.entity.BaseCategoryTrademark;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public interface BaseCategoryTrademarkMapper extends BaseMapper<BaseCategoryTrademark> {
    /**
     *  根据三级分类获取商家id集合
     * @param id
     * @return
     */
    List<Long> selectTrademarkIdsByCategory3Id(Long id);
}
