package com.wjl.gmall.cart.service;

import com.wjl.gmall.cart.model.entity.CartInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
public interface CartService {
    /**
     *  添加购物车，返回当前购物车数据
     * @param skuId
     * @param skuNum
     * @param userId
     * @return
     */
    void addToCart(Long skuId, Integer skuNum, String userId);

    /**
     * 查询购物车
     * @param userId
     * @param userTempId
     * @return
     */
    List<CartInfo> cartList(String userId, String userTempId);

    /**
     *  选中或者不选中购物车
     * @param skuId
     * @param isChecked
     * @param userId
     */
    void check(Long skuId, Integer isChecked, String userId);

    void deleteCartSku(Long skuId, String userId);

    List<CartInfo> getCartCheckedList(Long userId);

    void updateCartCache(Map<Long, BigDecimal> changeSkuIds, String userId);
}
