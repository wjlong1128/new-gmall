package com.wjl.gmall.cart.client.impl;

import com.wjl.gmall.cart.client.CartServiceClient;
import com.wjl.gmall.cart.model.dto.CartInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

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
        };
    }
}
