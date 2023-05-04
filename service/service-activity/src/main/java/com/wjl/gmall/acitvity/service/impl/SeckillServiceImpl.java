package com.wjl.gmall.acitvity.service.impl;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.acitvity.model.entity.OrderRecode;
import com.wjl.gmall.acitvity.model.entity.SeckillGoods;
import com.wjl.gmall.acitvity.model.entity.UserRecode;
import com.wjl.gmall.acitvity.model.vo.SeckillStatus;
import com.wjl.gmall.acitvity.model.vo.SeckillTradeVo;
import com.wjl.gmall.acitvity.model.vo.SubmitOrderVo;
import com.wjl.gmall.acitvity.redis.RedisChannelConfig;
import com.wjl.gmall.acitvity.repository.SeckillRepository;
import com.wjl.gmall.acitvity.service.SeckillService;
import com.wjl.gmall.acitvity.util.CacheHelper;
import com.wjl.gmall.common.constant.RedisConst;
import com.wjl.gmall.common.constants.MqConst;
import com.wjl.gmall.common.execption.BusinessException;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.result.ResultCodeEnum;
import com.wjl.gmall.common.service.RabbitService;
import com.wjl.gmall.common.util.MD5;
import com.wjl.gmall.order.client.OrderServiceClient;
import com.wjl.gmall.order.model.dto.OrderDetail;
import com.wjl.gmall.order.model.dto.OrderInfo;
import com.wjl.gmall.user.client.UserServiceClient;
import com.wjl.gmall.user.dto.UserAddress;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
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

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ThreadPoolExecutor executor;


    @Autowired
    private OrderServiceClient orderServiceClient;

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
        // 校验是否下过单 并设置用户记录
        String userOrderKey = getUserOrderKey(userId);
        Boolean exists = this.stringRedisTemplate.opsForValue().setIfAbsent(userOrderKey, skuId.toString());
        if (!Boolean.TRUE.equals(exists)) {
            return;
        }
        // 校验库存
        String seckillStockKey = getSeckillStockKey(skuId);
        String stockValue = this.stringRedisTemplate.opsForList().rightPop(seckillStockKey);
        if (StringUtils.isEmpty(stockValue)) {
            // 标记商品没有了
            this.stringRedisTemplate.convertAndSend(RedisChannelConfig.SECKILL_PUBLISH, skuId + ":0");
            return;
        }
        // 生成订单信息
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

    private static String getSeckillStockKey(Long skuId) {
        return RedisConst.SECKILL_STOCK_PREFIX + skuId;
    }


    /**
     * 获取用户已经下单key
     *
     * @param userId
     * @return
     */
    private static String getUserOrderKey(String userId) {
        return RedisConst.SECKILL_USER + userId;
    }

    @Override
    public void updateStockCount(Long skuId) {
        RLock lock = redissonClient.getLock("seckill:lock:" + skuId);
        lock.lock();
        try {
            // 获取最新数量
            Long stockSize = this.stringRedisTemplate.boundListOps(getSeckillStockKey(skuId)).size();
            if (stockSize.intValue() == 0) {
                // 更新数据库
                this.seckillRepository.updateSeckillGoodsStock(skuId, stockSize.intValue());
            }
            // 更新redis
            String json = (String) this.stringRedisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS, skuId.toString());
            SeckillGoods seckillGoods = JSON.parseObject(json, SeckillGoods.class);
            seckillGoods.setStockCount(stockSize.intValue());
            this.stringRedisTemplate.opsForHash().put(RedisConst.SECKILL_GOODS, skuId.toString(), JSON.toJSONString(seckillGoods));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SeckillStatus checkOrder(Long skuId, String userId) {
        SeckillStatus seckillStatus = new SeckillStatus();
        String userHistoryKey = RedisConst.SECKILL_USER + userId;
        String skuStr = this.stringRedisTemplate.opsForValue().get(userHistoryKey);
        // 判断是否下过秒杀单
        if (StringUtils.isEmpty(skuStr) || !skuStr.equals(skuId.toString())) {
            // 没有下过单的理由 1. 状态位没了-> 秒杀结束  2.有 -> 还是在排队中
            String status = (String) CacheHelper.get(skuStr.toString());
            if ("0".equals(status)) {
                seckillStatus.setStatus(ResultCodeEnum.SECKILL_FINISH);
                return seckillStatus;
            }
        }
        // 获取临时订单
        String orderUserKey = getOrderUserKey(skuId);
        BoundHashOperations<String, String, String> seckillOrderSkuBound =
                this.stringRedisTemplate.boundHashOps(orderUserKey);
        Boolean isExists = seckillOrderSkuBound.hasKey(userId);
        if (Boolean.TRUE.equals(isExists)) {
            // 说明用户未支付 但是下单了 抢单成功
            seckillStatus.setStatus(ResultCodeEnum.SECKILL_SUCCESS);
            String json = seckillOrderSkuBound.get(userId);
            OrderRecode orderRecode = JSON.parseObject(json, OrderRecode.class);
            seckillStatus.setData(orderRecode);
            return seckillStatus;
        }
        // 没有 可能支付了 看看支付订单有没有
        BoundHashOperations<String, String, String> skuOrderUser =
                this.stringRedisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS_USERS + skuId);
        isExists = skuOrderUser.hasKey(userId);
        if (Boolean.TRUE.equals(isExists)) {
            // 支付了
            seckillStatus.setStatus(ResultCodeEnum.SECKILL_ORDER_SUCCESS);
            // 返回订单id
            String orderId = skuOrderUser.get(userId);
            seckillStatus.setData(orderId);
            return seckillStatus;
        }
        // 没有 在排队
        seckillStatus.setStatus(ResultCodeEnum.SECKILL_RUN);
        return seckillStatus;
    }

    /**
     * 获取抢单成功的的用户的key
     *
     * @param skuId
     * @return
     */
    private static String getOrderUserKey(Long skuId) {
        return RedisConst.SECKILL_ORDERS + skuId;
    }

    /**
     * 获取秒杀下单页信息
     *
     * @param skuId
     * @param userId
     * @return
     */
    @Override
    public SeckillTradeVo getTradeData(String skuId, String userId) {
        SeckillTradeVo seckillTradeVo = new SeckillTradeVo();
        CompletableFuture<Void> orderDetailFuture = CompletableFuture.runAsync(() -> {
            // 获取送货清单
            BoundHashOperations<String, String, String> boundSeckillOrders = this.stringRedisTemplate.boundHashOps(RedisConst.SECKILL_ORDERS + skuId);
            String json = boundSeckillOrders.get(userId);
            if (StringUtils.isEmpty(json)) {
                throw new BusinessException(ResultCodeEnum.ILLEGAL_REQUEST);
            }
            OrderRecode orderRecode = JSON.parseObject(json, OrderRecode.class);

            // 获取秒杀商品
            SeckillGoods seckillGoods = orderRecode.getSeckillGoods();
            List<OrderDetail> orderDetailList = new ArrayList<>();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(seckillGoods.getSkuId());
            orderDetail.setSkuName(seckillGoods.getSkuName());
            orderDetail.setImgUrl(seckillGoods.getSkuDefaultImg());
            // 下单价格为秒杀价格
            orderDetail.setOrderPrice(seckillGoods.getCostPrice());
            orderDetail.setSkuNum(orderRecode.getNum());
            orderDetailList.add(orderDetail);

            // 计算总金额
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderDetailList(orderDetailList);
            orderInfo.sumTotalAmount();

            seckillTradeVo.setTotalNum(orderRecode.getNum());
            seckillTradeVo.setTotalAmount(orderInfo.getTotalAmount());
            seckillTradeVo.setDetailArrayList(orderDetailList);

        }, executor);

        CompletableFuture<Void> addressListFuture = CompletableFuture.runAsync(() -> {
            // 获取用户地址
            List<UserAddress> userAddressListByUserId = this.userServiceClient.findUserAddressListByUserId(Long.parseLong(userId));
            seckillTradeVo.setUserAddressList(userAddressListByUserId);
        }, executor);
        CompletableFuture.allOf(addressListFuture, orderDetailFuture).join();
        return seckillTradeVo;
    }

    /**
     * 提交订单信息
     *
     * @param userId
     * @param submitOrderVo
     * @return
     */
    @Override
    public Long submitOrder(String userId, SubmitOrderVo submitOrderVo) {
        OrderInfo order = submitOrderVo.getOrder();
        order.setUserId(Long.parseLong(userId));
        // 获取流水号
        String tradeNo = this.orderServiceClient.getTradeNo().getData();
        // 提交订单
        Result<Long> orderSubmit = this.orderServiceClient.submitOrder(order, tradeNo, true);
        if (!orderSubmit.isOk()) {
            throw new BusinessException("下单失败");
        }
        Long orderInfoId = orderSubmit.getData();
        Long skuId = submitOrderVo.getSkuId();
        // 处理临时订单
        BoundHashOperations<String, String, String> tempOrder = this.stringRedisTemplate.boundHashOps(getOrderUserKey(skuId));
        tempOrder.delete(userId);
        // 添加用户订单记录

        BoundHashOperations<String, String, String> seckillOrder = this.stringRedisTemplate.boundHashOps(getOrderInfoSeckillKey(skuId));
        seckillOrder.put(userId, orderInfoId.toString());
        return orderInfoId;
    }

    @Override
    public void clearOverDueSeckill() {
        List<Long> ids = this.seckillRepository.getOverDueSeckillSkuIds();
        ids.forEach(skuId->{
            // 删除对应的商品
            this.stringRedisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).delete(skuId.toString());
            // 删除库存
            this.stringRedisTemplate.delete(RedisConst.SECKILL_STOCK_PREFIX + skuId);
            // 删除预下单
            this.stringRedisTemplate.delete(RedisConst.SECKILL_USER + skuId);
            // 删除下单
            this.stringRedisTemplate.delete(RedisConst.SECKILL_ORDERS_USERS + skuId);
        });
    }

    /**
     * 获取已经提交订单的用户的key
     *
     * @param skuId
     * @return
     */
    private static String getOrderInfoSeckillKey(Long skuId) {
        return RedisConst.SECKILL_ORDERS_USERS + skuId;
    }


    private static String getRandomCodeKey(Long skuId, String userId) {
        return "user:" + userId + ":seckill:" + skuId + ":code";
    }


}
