package com.wjl.gmall.order.client;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.order.client.impl.OrderDegradeServiceClienFallbackFactory;
import com.wjl.gmall.order.model.dto.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 获取流水号
     *
     * @return
     */
    @GetMapping("api/order/inner/tradeNo")
    public Result<String> getTradeNo();


    /**
     * 提交订单 返回订单id
     * 验证价格是否变更，变更则将变更的数据发送给cart服务更新购物扯数据
     * 调用库存服务查看库存是否足够
     *
     * @param orderInfo
     * @param tradeNo   防止重复提交的流水号
     * @return 返回订单号
     */
    @PostMapping("api/order/auth/submitOrder")
    public Result<Long> submitOrder(@RequestBody OrderInfo orderInfo, @RequestParam("tradeNo") String tradeNo, @RequestParam(value = "isActivity", required = false, defaultValue = "false") Boolean isActivity);

}