package com.wjl.gmall.cart.client;

import com.wjl.gmall.cart.client.impl.CartServiceClientFallFactory;
import com.wjl.gmall.cart.model.dto.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@FeignClient(value = "service-cart",fallbackFactory = CartServiceClientFallFactory.class)
public interface CartServiceClient {

    /**
     *  获取已经选中的购物车列表
     * @param userId
     * @return
     */
    @GetMapping("/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId);
}
