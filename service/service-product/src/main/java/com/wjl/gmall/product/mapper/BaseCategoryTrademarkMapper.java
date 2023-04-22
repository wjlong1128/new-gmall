package com.wjl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gmall.model.product.BaseCategoryTrademark;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public interface BaseCategoryTrademarkMapper extends BaseMapper<BaseCategoryTrademark> {
    List<Long> selectTrademarkIdsByCategory3Id(Long id);
}
