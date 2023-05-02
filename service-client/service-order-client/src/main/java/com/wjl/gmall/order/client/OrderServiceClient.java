package com.wjl.gmall.order.client;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.order.client.impl.OrderDegradeServiceClienFallbackFactory;
import com.wjl.gmall.order.model.dto.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@FeignClient(value = "service-order", fallbackFactory = OrderDegradeServiceClienFallbackFactory.class)
public interface OrderServiceClient {

    /**
     * 获取交易信息
     *
     * @return
     */
    @GetMapping("api/order/auth/trade")
    public Result<Map<String, Object>> trade();

    /**
     * 订单付款页面信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("api/order/inner/getOrderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable("orderId") Long orderId);


    /**
     * 订单付款页面信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("api/order/inner/getOrderInfoUnpaid/{orderId}")
    public Result<OrderInfo> getOrderUnpaid(@PathVariable("orderId") Long orderId);

    @GetMapping("api/order/inner/updateOrderUpdate/{orderId}")
    Result updateOrderStatus(@PathVariable("orderId") String tradeNo, @RequestParam("status") String orderStatus);
}
