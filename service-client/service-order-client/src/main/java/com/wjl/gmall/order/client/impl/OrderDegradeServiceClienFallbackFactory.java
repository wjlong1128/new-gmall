package com.wjl.gmall.order.client.impl;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.order.client.OrderServiceClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@Component
public class OrderDegradeServiceClienFallbackFactory implements FallbackFactory<OrderServiceClient> {
    @Override
    public OrderServiceClient create(Throwable cause) {
        return new OrderServiceClient() {
            @Override
            public Result<Map<String, Object>> trade() {
                return Result.fail();
            }
        };
    }
}
