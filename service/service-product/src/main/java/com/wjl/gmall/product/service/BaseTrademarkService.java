package com.wjl.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.gmall.product.model.entity.BaseTrademark;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public interface BaseTrademarkService extends IService<BaseTrademark> {
    IPage<BaseTrademark> getBaseTrademarkList(Long page, Long limit);
    /**
     *  根据三级分类获取商家集合
     * @param id
     * @return
     */
    List<BaseTrademark> findTrademarkListByCategory3Id(Long id);

    /**
     * 删除三级分类和商家品牌的关联信息
     * @param category3Id
     * @param trademarkId
     */
    void removeCategoryTrademark(Long category3Id, Long trademarkId);
}
