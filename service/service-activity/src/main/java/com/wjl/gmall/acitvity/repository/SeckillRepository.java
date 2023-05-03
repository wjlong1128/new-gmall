package com.wjl.gmall.acitvity.repository;

import com.wjl.gmall.acitvity.model.entity.SeckillGoods;

import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
public interface SeckillRepository {

    /**
     * 获取最近几天的秒杀
     *
     * @param day
     * @return
     */
    List<SeckillGoods> getSeckillsWithLastDay(int day);

    /**
     *  更新仓库数量
     * @param skuId
     * @param stockSize
     */
    void updateSeckillGoodsStock(Long skuId, Integer stockSize);
}
