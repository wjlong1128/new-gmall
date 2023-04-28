package com.wjl.gmall.order.client;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.order.client.impl.OrderDegradeServiceClienFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@FeignClient(value = "service-order",fallbackFactory = OrderDegradeServiceClienFallbackFactory.class)
public interface OrderServiceClient {

    /**
     * 获取交易信息
     *
     * @return
     */
    @GetMapping("api/order/auth/trade")
    public Result<Map<String, Object>> trade();
}
