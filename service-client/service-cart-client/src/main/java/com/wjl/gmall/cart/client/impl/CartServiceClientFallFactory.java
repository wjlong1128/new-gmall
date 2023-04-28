package com.wjl.gmall.cart.client.impl;

import com.wjl.gmall.cart.client.CartServiceClient;
import com.wjl.gmall.cart.model.dto.CartInfo;
import com.wjl.gmall.common.result.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@Component
public class CartServiceClientFallFactory implements FallbackFactory<CartServiceClient> {
    @Override
    public CartServiceClient create(Throwable cause) {
        return new CartServiceClient() {
            @Override
            public List<CartInfo> getCartCheckedList(Long userId) {
                return null;
            }

            @Override
            public Result updateCartCache(Map<Long, BigDecimal> changeSkuIds, String userId) {
                return null;
            }
        };
    }
}
