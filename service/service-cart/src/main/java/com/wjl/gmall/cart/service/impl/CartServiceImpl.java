package com.wjl.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.cart.model.entity.CartInfo;
import com.wjl.gmall.cart.service.CartService;
import com.wjl.gmall.common.constant.RedisConst;
import com.wjl.gmall.product.client.ProductServiceClient;
import com.wjl.gmall.product.client.model.dto.SkuInfo;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductServiceClient productServiceClient;

    /**
     * 添加购物车返回购物车当前商品
     * redis中取数据
     * 如果没有，就创建 并且 添加购物车价格和实时价格
     * 如果有，那么不用更新购物车价格，只用更新实时价格
     * redis:
     * {userId: {skuId,skuNum}}
     *
     * @param skuId  商品id
     * @param skuNum 商品数量
     * @param userId 用户id 或着临时用户id
     * @return
     */
    @Override
    public void addToCart(Long skuId, Integer skuNum, String userId) {
        String cartKey = getKey(userId);
        BoundHashOperations<String, Object, Object> boundCart = stringRedisTemplate.boundHashOps(cartKey);
        CartInfo info = null;
        // 是否存在该类型的商品
        String skuKey = skuId.toString();
        if (boundCart.hasKey(skuKey)) {
            String json = (String) boundCart.get(skuKey);
            if (StringUtils.hasText(json)) {
                info = JSON.parseObject(json, CartInfo.class);
                // 更新时间
                info.setUpdateTime(new Date());
                info.setIsChecked(1);
                info.setUserId(userId);
                // 数量
                info.setSkuNum(info.getSkuNum() + skuNum);
                // 实时jiage
                BigDecimal price = productServiceClient.getSkuPrice(skuId).getData();
                info.setSkuPrice(price);
                boundCart.put(skuKey, JSON.toJSONString(info));
                boundCart.expire(RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
                // TODO 定时任务更新redis数据至数据库
                return;
            }
        }

        // 来到这里，证明hash中没有skuId的key,或者json数据为空
        info = new CartInfo();
        info.setSkuId(skuId);
        info.setUserId(userId);
        info.setCreateTime(new Date());
        info.setUpdateTime(new Date());
        SkuInfo sku = productServiceClient.getSkuInfoAndImages(skuId).getData();
        info.setSkuName(sku.getSkuName());
        info.setCartPrice(sku.getPrice());
        info.setCartPrice(sku.getPrice());
        info.setImgUrl(sku.getSkuDefaultImg());
        info.setSkuNum(skuNum);
        // 存入redis
        boundCart.put(skuKey, JSON.toJSONString(info));
        boundCart.expire(RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

    private static String getKey(String userId) {
        String cartKey = RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
        return cartKey;
    }

    /**
     * 查询购物车 查询redis中的所有购物车信息，如果以下条件都不满足那么返回空集合
     * 如果登录了就合并购物车数据
     *
     * @param userId
     * @param userTempId
     * @return
     */
    @Override
    public List<CartInfo> cartList(String userId, String userTempId) {
        ArrayList<CartInfo> noLoginCartList = new ArrayList<>();
        boolean isNotEmpty = false;
        if (StringUtils.hasText(userTempId)) {
            isNotEmpty = getRedisCartToList(userTempId, noLoginCartList);
        }

        if (StringUtils.isEmpty(userId)) { // 没有登录
            if (isNotEmpty) {
                noLoginCartList.sort((a, b) -> {
                    return DateUtils.truncatedCompareTo(a.getUpdateTime(), b.getUpdateTime(), Calendar.SECOND);
                });
            }
            return noLoginCartList;// 不会为 null
        }

        // 登录了 合并
        String userCartKey = getKey(userId);
        if (isNotEmpty) { // 登录了并且 没登陆的购物车有值
            BoundHashOperations<String, Object, Object> boundUserCartKey = stringRedisTemplate.boundHashOps(userCartKey);
            noLoginCartList.forEach(item -> {
                String skuId = item.getSkuId().toString();
                if (boundUserCartKey.hasKey(skuId)) {
                    // 存在 添加数量
                    String json = (String) boundUserCartKey.get(skuId);
                    CartInfo cartInfo = JSON.parseObject(json, CartInfo.class);
                    cartInfo.setSkuNum(cartInfo.getSkuNum() + item.getSkuNum());
                    // 更新时间
                    if (DateUtils.truncatedCompareTo(cartInfo.getUpdateTime(), item.getUpdateTime(), Calendar.SECOND) < 0) {
                        cartInfo.setUpdateTime(item.getUpdateTime());
                    }
                    // 选中
                    if (item.getIsChecked().intValue() == 1) {
                        cartInfo.setIsChecked(1);
                    }
                    boundUserCartKey.put(skuId, JSON.toJSONString(cartInfo));// 更新redis数据
                } else {
                    // 不存在 添加信息
                    item.setUserId(userId);// 更新用户
                    boundUserCartKey.put(skuId, JSON.toJSONString(item)); // 添加redis
                }
            });
        }

        // 查出所有登录用户的购物车的信息
        ArrayList<CartInfo> loginCartList = new ArrayList<>();
        boolean loginIsNotEmpty = getRedisCartToList(userId, loginCartList);
        if (loginIsNotEmpty) {
            loginCartList.sort((a, b) -> {
                return DateUtils.truncatedCompareTo(a.getUpdateTime(), b.getUpdateTime(), Calendar.SECOND);
            });
        }

        // 删除临时用户购物车
        stringRedisTemplate.delete(getKey(userTempId));
        return loginCartList;
    }

    @Override
    public void check(Long skuId, Integer isChecked, String userId) {
        String cartKey = getKey(userId);
        BoundHashOperations<String, String, String> boundCartKey = stringRedisTemplate.boundHashOps(cartKey);
        String filed = skuId.toString();
        if (boundCartKey.hasKey(filed)) {
            String json = boundCartKey.get(filed);
            if (StringUtils.hasText(json)) {
                CartInfo info = JSON.parseObject(json, CartInfo.class);
                isChecked = isChecked != null ? isChecked : 0;
                info.setIsChecked(isChecked);
                boundCartKey.put(filed, JSON.toJSONString(info));
            }
        }
    }


    @Override
    public void deleteCartSku(Long skuId, String userId) {
        String cartKey = getKey(userId);
        stringRedisTemplate.opsForHash().delete(cartKey, skuId.toString());
    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        ArrayList<CartInfo> cartInfos = new ArrayList<>();
        boolean isNotEmpty = getRedisCartToList(userId.toString(), cartInfos);
        if (isNotEmpty) {
            return cartInfos
                    .stream()
                    .filter(item -> item.getIsChecked().equals(1))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    /**
     * 将redis中的cart数据添加到指定的集合中
     * 并且集合添加上了数据
     *
     * @param id
     * @param cartInfos
     * @return
     */
    private boolean getRedisCartToList(String id, ArrayList<CartInfo> cartInfos) {
        boolean flag = false;
        if (StringUtils.hasText(id)) {
            String cartKey = getKey(id);
            List<Object> values = stringRedisTemplate.opsForHash().values(cartKey);
            if (!CollectionUtils.isEmpty(values)) {
                flag = true;
                values.forEach(value -> {
                    String json = (String) value;
                    if (StringUtils.hasText(json)) {
                        cartInfos.add(JSON.parseObject(json, CartInfo.class));
                    }
                });
            }
        }
        return flag;
    }
}
