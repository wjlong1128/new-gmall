package com.wjl.gmall.acitvity.service.impl;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.acitvity.model.entity.OrderRecode;
import com.wjl.gmall.acitvity.model.entity.SeckillGoods;
import com.wjl.gmall.acitvity.model.entity.UserRecode;
import com.wjl.gmall.acitvity.redis.RedisChannelConfig;
import com.wjl.gmall.acitvity.repository.SeckillRepository;
import com.wjl.gmall.acitvity.service.SeckillService;
import com.wjl.gmall.acitvity.util.CacheHelper;
import com.wjl.gmall.common.constant.RedisConst;
import com.wjl.gmall.common.constants.MqConst;
import com.wjl.gmall.common.execption.BusinessException;
import com.wjl.gmall.common.result.ResultCodeEnum;
import com.wjl.gmall.common.service.RabbitService;
import com.wjl.gmall.common.util.MD5;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillRepository seckillRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取需要预热的商品
     *
     * @param day
     */
    @Override
    public void preheatSeckill(int day) {
        List<SeckillGoods> seckillGoods = this.seckillRepository.getSeckillsWithLastDay(day);
        // 将数据存入redis
        Map<String, String> goodsCache = new HashMap<>(seckillGoods.size());
        Map<String, List<String>> goodsStocks = new HashMap<>(seckillGoods.size());
        BoundHashOperations<String, String, String> goodsCacheBound = this.stringRedisTemplate.boundHashOps(RedisConst.SECKILL_GOODS);
        Set<String> keys = goodsCacheBound.keys();
        seckillGoods.forEach(goods -> {
            String skuId = goods.getSkuId().toString();
            if (!keys.contains(skuId)) {
                goodsCache.put(skuId, JSON.toJSONString(goods));
                // skuId: [23,23,23] 数量为stock量
                List<String> stocks = new ArrayList<>();
                for (int i = 0; i < goods.getStockCount(); i++) {
                    stocks.add(skuId);
                }
                goodsStocks.put(skuId, stocks);
            }
        });
        // 将数据存入redis中 {key: {skuId,seckillGoods}}
        this.stringRedisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).putAll(goodsCache);
        // 防止库存超卖的key
        goodsStocks.forEach((skuId, stock) -> {
            // 作用 防止超卖
            this.stringRedisTemplate.opsForList().leftPushAll(RedisConst.SECKILL_STOCK_PREFIX + skuId, stock);
            // 发布状态位 在内存中添加一个当前商品可抢购的标识
            this.stringRedisTemplate.convertAndSend(RedisChannelConfig.SECKILL_PUBLISH, skuId + ":1");
        });

    }

    /**
     * 查询所有秒杀
     *
     * @return
     */
    @Override
    public List<SeckillGoods> findAll() {
        List<Object> values = stringRedisTemplate.opsForHash().values(RedisConst.SECKILL_GOODS);
        List<SeckillGoods> all = values.stream().map(item -> JSON.parseObject((String) item, SeckillGoods.class)).collect(Collectors.toList());
        return all;
    }

    /**
     * 查询单个秒杀商品
     *
     * @param skuId
     * @return
     */
    @Override
    public SeckillGoods findBySkuId(Long skuId) {
        Object json = this.stringRedisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, skuId.toString());
        if (json != null && json instanceof String && StringUtils.hasText((String) json)) {
            return JSON.parseObject((String) json, SeckillGoods.class);
        }
        return null;
    }

    /**
     * 根据用户id和skuid 判断所秒杀的商品是否在秒杀的时间点内，生成秒杀随机码，
     * 前端抢单时根据此随机码下单
     *
     * @param skuId
     * @param userId
     * @return
     */
    @Override
    public String getRandomCode(Long skuId, String userId) {
        BoundHashOperations<String, String, String> boundGoods = this.stringRedisTemplate.boundHashOps(RedisConst.SECKILL_GOODS);
        if (boundGoods.hasKey(skuId.toString())) {
            String json = boundGoods.get(skuId.toString());
            if (StringUtils.hasText(json)) {
                SeckillGoods seckillGoods = JSON.parseObject(json, SeckillGoods.class);
                LocalDateTime now = LocalDateTime.now();
                // 判断是否在抢购时间点内
                if (now.compareTo(seckillGoods.getEndTime()) < 0 && now.compareTo(seckillGoods.getStartTime()) >= 0) {
                    String randomCode = MD5.encrypt(userId);
                    // 存入redis
                    String key = getRandomCodeKey(skuId, userId);
                    this.stringRedisTemplate.opsForValue().set(key, randomCode, 1, TimeUnit.DAYS);
                    return randomCode;
                }
            }
        }
        throw new RuntimeException("获取抢单码失败");
    }

    @Override
    public void seckillOrder(Long skuId, String userId, String randomCode) {
        String randomCodeKey = getRandomCodeKey(skuId, userId);
        String code = this.stringRedisTemplate.opsForValue().get(randomCodeKey);
        if (StringUtils.hasText(code) && code.equals(randomCode)) {
            // 校验成功
            // 校验状态位
            String status = (String) CacheHelper.get(skuId.toString());
            if ("0".equals(status)) {
                throw new BusinessException(ResultCodeEnum.SECKILL_FINISH);
            }

            if ("1".equals(status)) {
                // 可以生成抢单对象了
                UserRecode userRecode = new UserRecode();
                userRecode.setSkuId(skuId);
                userRecode.setUserId(userId);
                // 发送给处理秒杀的监听器
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SECKILL_USER, MqConst.ROUTING_SECKILL_USER, userRecode);
                this.stringRedisTemplate.delete(randomCodeKey);
                return;
            }
        }
        throw new RuntimeException("请求不合法");

    }


    /**
     * 处理抢单
     *
     * @param userId
     * @param skuId
     */
    @Override
    public void seckillOrderProcess(String userId, Long skuId) {
        // 校验状态位
        String status = (String) CacheHelper.get(skuId.toString());
        if ("0".equals(status) || StringUtils.isEmpty(status)) {
            return;
        }
        // 校验是否下过单
        Boolean exists = this.stringRedisTemplate.opsForValue().setIfAbsent(RedisConst.SECKILL_USER + userId, skuId.toString());
        if (!Boolean.TRUE.equals(exists)) {
            return;
        }
        // 校验库存
        String stockValue = this.stringRedisTemplate.opsForList().rightPop(RedisConst.SECKILL_STOCK_PREFIX + skuId);
        if (StringUtils.isEmpty(stockValue)) {
            // 标记没有了
            this.stringRedisTemplate.convertAndSend(RedisChannelConfig.SECKILL_PUBLISH, skuId + ":0");
            return;
        }
        // 生成订单信息、
        OrderRecode orderRecode = new OrderRecode();
        orderRecode.setUserId(userId);
        String json = (String) this.stringRedisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, skuId.toString());
        orderRecode.setSeckillGoods(JSON.parseObject(json, SeckillGoods.class));
        orderRecode.setNum(1);
        // 订单码
        orderRecode.setOrderStr(MD5.encrypt(userId + skuId));
        // TODO 注意
        this.stringRedisTemplate.opsForHash().put(RedisConst.SECKILL_ORDERS + skuId, userId, JSON.toJSONString(orderRecode));
        // 更新库存
        this.updateStockCount(skuId);
    }

    @Override
    public void updateStockCount(Long skuId) {
        RLock lock = redissonClient.getLock("seckill:lock:" + skuId);
        lock.lock();
        try {
            // 获取最新数量
            Long stockSize = this.stringRedisTemplate.boundListOps(RedisConst.SECKILL_STOCK_PREFIX + skuId).size();
            // 更新数据库
            this.seckillRepository.updateSeckillGoodsStock(skuId, stockSize.intValue());
            // 更新redis
            String json = (String) this.stringRedisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, skuId.toString());
            SeckillGoods seckillGoods = JSON.parseObject(json, SeckillGoods.class);
            seckillGoods.setStockCount(stockSize.intValue());
            this.stringRedisTemplate.opsForHash().put(RedisConst.SECKILL_GOODS, skuId.toString(), JSON.toJSONString(seckillGoods));
        } finally {
            lock.unlock();
        }
    }

    private static String getRandomCodeKey(Long skuId, String userId) {
        return "user:" + userId + ":seckill:" + skuId + ":code";
    }

}
