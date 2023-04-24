package com.wjl.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.gmall.common.constant.RedisConst;
import com.wjl.gmall.product.model.entity.SkuAttrValue;
import com.wjl.gmall.product.model.entity.SkuImage;
import com.wjl.gmall.product.model.entity.SkuInfo;
import com.wjl.gmall.product.model.entity.SkuSaleAttrValue;
import com.wjl.gmall.product.mapper.SkuAttrValueMapper;
import com.wjl.gmall.product.mapper.SkuImageMapper;
import com.wjl.gmall.product.mapper.SkuInfoMapper;
import com.wjl.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.wjl.gmall.product.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.wjl.gmall.common.constant.RedisConst.SKUKEY_TEMPORARY_TIMEOUT;
import static com.wjl.gmall.common.constant.RedisConst.SKUKEY_TIMEOUT;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@Slf4j
@Service
public class SkuServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuAttrValueMapper attrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 保存一个sku（商品）信息及其所关联的信息
     *
     * @param skuInfo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSku(SkuInfo skuInfo) {
        skuInfo.setIsSale(0);
        List<SkuImage> images = skuInfo.getSkuImageList();
        images.forEach(image -> {
            if (image.getIsDefault().equals("1")) {
                skuInfo.setSkuDefaultImg(image.getImgUrl());
            }
        });

        skuInfoMapper.insert(skuInfo);
        images.forEach(image -> {
            image.setSkuId(skuInfo.getId());
            // 循环操作数据库...
            skuImageMapper.insert(image);
        });

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            skuAttrValueList.forEach(attrValue -> {
                attrValue.setSkuId(skuInfo.getId());
                attrValueMapper.insert(attrValue);
            });
        }

        List<SkuSaleAttrValue> saleAttrValues = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(saleAttrValues)) {
            saleAttrValues.forEach(s -> {
                s.setSkuId(skuInfo.getId());
                s.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(s);
            });
        }

        // 将数据添加到布隆过滤器 告知布隆过滤器是否存在该数据
        RBloomFilter<Object> skuBloomFilter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER);
        skuBloomFilter.add(skuInfo.getId());
    }

    /**
     * 上架
     *
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        SkuInfo info = new SkuInfo();
        info.setIsSale(1);
        info.setId(skuId);
        updateById(info);
    }

    /**
     * 下架
     *
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo info = new SkuInfo();
        info.setIsSale(0);
        info.setId(skuId);
        updateById(info);
    }

    /**
     * 根据sku获取所属sku的基本信息以及sku图片信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoAndImages(Long skuId) {
        try {
            return getSkuInfoAndImagesWithRedis(skuId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // redis出问题了
        return getSkuAndImagesDB(skuId);
    }

    private SkuInfo getSkuInfoAndImagesWithRedis(Long skuId) {
        String key = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        // 1. 查询缓存
        String json = redisTemplate.opsForValue().get(key);
        // 2. 缓存中有值 反序列化为对象返回
        if (StringUtils.hasText(json)) {
            return JSON.parseObject(json, SkuInfo.class);
        }

        // 3. 如果没有
        String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
        RLock lock = redissonClient.getLock(lockKey);
        // 3.1 上锁
        lock.lock();
        try {
            // 3.2 再次判断缓存中有没有数据 避免并发情况下循环查库
            json = redisTemplate.opsForValue().get(key);
            if (StringUtils.hasText(json)) {
                return JSON.parseObject(json, SkuInfo.class);
            }
            // 3.3 这里真的没有的话，再查库 将查询到的对象 不为空序列化为json缓存，为空则缓存一个短时间的空对象
            SkuInfo skuInfo = getSkuAndImagesDB(skuId);
            if (skuInfo == null) {
                // 防止击穿
                log.info("缓存对象为空对象:{}", skuId);
                redisTemplate.opsForValue().set(key, "{}", SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                return skuInfo;
            }
            json = JSON.toJSONString(skuInfo);
            int randomValue = new Random().nextInt(100);  // 缓存时间随机值
            redisTemplate.opsForValue().set(key, json, SKUKEY_TIMEOUT + randomValue, TimeUnit.SECONDS);
            return skuInfo;
        } finally {
            // 3.4 必须解锁
            lock.unlock();
        }
    }


    /**
     * 数据库查询
     *
     * @param skuId
     * @return
     */
    private SkuInfo getSkuAndImagesDB(Long skuId) {
        log.info("命中数据库 skuId:{}", skuId);
        SkuInfo skuInfo = this.getById(skuId);
        if (skuInfo == null) {
            return null;
        }
        Long skuInfoId = skuInfo.getId();
        List<SkuImage> skuImages = skuImageMapper.getSkuImages(skuInfoId);
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }

    /**
     * 获取sku实时价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return this.baseMapper.getPrice(skuId);
    }
}
