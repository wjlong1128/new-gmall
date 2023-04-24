package com.wjl.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.gmall.model.product.BaseCategoryTrademark;
import com.wjl.gmall.model.product.BaseTrademark;
import com.wjl.gmall.model.product.CategoryTrademarkVo;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public interface BaseCategoryTrademarkService extends IService<BaseCategoryTrademark> {
    /**
     *  查询出未被此分类关联的企业
     * @param category3Id
     * @return
     */
    List<BaseTrademark> findCurrentTrademarkList(Long category3Id);

    /**
     *  保存商家与分类的关联信息
     * @param categoryTrademarkVo
     */
    void save(CategoryTrademarkVo categoryTrademarkVo);
}
