package com.wjl.gmall.acitvity.service;

import com.wjl.gmall.acitvity.model.entity.SeckillGoods;

import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
public interface SeckillService {

    /**
     *  预热最近几天的商品
     * @param day
     */
    void preheatSeckill(int day);

    /**
     *  获取所有秒杀商品
     * @return
     */
    List<SeckillGoods> findAll();

    /**
     *  获取单个秒杀商品
     * @param skuId
     * @return
     */
    SeckillGoods findBySkuId(Long skuId);

    /**
     *  获取下单随机码
     * @param skuId
     * @param userId
     * @return
     */
    String getRandomCode(Long skuId, String userId);

    /**
     *  抢单
     * @param skuId
     * @param userId
     * @param randomCode
     */
    void seckillOrder(Long skuId, String userId, String randomCode);

    /**
     *  处理抢单
     * @param userId
     * @param skuId
     */
    void seckillOrderProcess(String userId, Long skuId);

    /**
     *  更新库存
     * @param skuId
     */
    void updateStockCount(Long skuId);
}
