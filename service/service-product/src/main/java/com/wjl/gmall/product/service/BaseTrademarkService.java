package com.wjl.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.gmall.model.product.BaseTrademark;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public interface BaseTrademarkService extends IService<BaseTrademark> {
    IPage<BaseTrademark> getBaseTrademarkList(Long page, Long limit);

    List<BaseTrademark> findTrademarkListByCategory3Id(Long id);

    void removeCategoryTrademark(Long category3Id, Long trademarkId);
}
