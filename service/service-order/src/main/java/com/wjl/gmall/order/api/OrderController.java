package com.wjl.gmall.order.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.util.AuthContextHolder;
import com.wjl.gmall.model.enums.OrderStatus;
import com.wjl.gmall.model.enums.ProcessStatus;
import com.wjl.gmall.order.model.entity.OrderInfo;
import com.wjl.gmall.order.model.vo.OrderWareVO;
import com.wjl.gmall.order.model.vo.WareSkuVo;
import com.wjl.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@RequestMapping("api/order")
@RestController
public class OrderController {


    @Autowired
    private OrderService orderService;

    /**
     * 获取交易信息 包括流水号
     *
     * @param request
     * @return
     */
    @GetMapping("auth/trade")
    public Result<Map<String, Object>> trade(HttpServletRequest request) {
        // 用户id
        String userId = AuthContextHolder.getUserId(request);
        Map<String, Object> result = orderService.trade(userId);
        return Result.ok(result);
    }

    /**
     * 获取流水号
     *
     * @param request
     * @return
     */
    @GetMapping("inner/tradeNo")
    public Result<String> getTradeNo(HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        String tradeNoCode = this.orderService.getTradeNoCode(userId);
        return Result.ok(tradeNoCode);
    }


    /**
     * 提交订单 返回订单id
     * 验证价格是否变更，变更则将变更的数据发送给cart服务更新购物扯数据
     * 调用库存服务查看库存是否足够
     *
     * @param orderInfo
     * @param request
     * @param tradeNo   防止重复提交的流水号
     * @return 返回订单号
     */
    @PostMapping("auth/submitOrder")
    public Result<Long> submitOrder(
            @RequestBody OrderInfo orderInfo,
            @RequestParam("tradeNo") String tradeNo,
            @RequestParam(value = "isActivity", defaultValue = "false", required = false) Boolean isActivity,
            HttpServletRequest request
    ) {
        String userId = AuthContextHolder.getUserId(request);
        Long orderId = this.orderService.submitOrder(orderInfo, tradeNo, userId, Boolean.TRUE.equals(isActivity));
        return Result.ok(orderId);
    }

    /**
     * 我的订单
     *
     * @param request
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("auth/{page}/{limit}")
    public Result<IPage<OrderInfo>> getOrders(HttpServletRequest request, @PathVariable("page") Long page, @PathVariable("limit") Long limit) {
        String userId = AuthContextHolder.getUserId(request);
        IPage<OrderInfo> orderInfoIPage = orderService.getOrders(userId, page, limit);
        return Result.ok(orderInfoIPage);
    }

    /**
     * 订单付款页面信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("inner/getOrderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable("orderId") Long orderId, HttpServletRequest request) {
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        return Result.ok(orderInfo);
    }

    /**
     * 订单付款页面信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("inner/getOrderInfoUnpaid/{orderId}")
    public Result<OrderInfo> getOrderUnpaid(@PathVariable("orderId") Long orderId, HttpServletRequest request) {
        OrderInfo orderInfo = orderService.getOrderInfoWithStatus(orderId, OrderStatus.UNPAID);
        return Result.ok(orderInfo);
    }


    /**
     * 更改订单支付状态
     *
     * @param tradeNo
     * @param orderStatus
     * @return
     */
    @GetMapping("inner/updateOrderUpdate/{orderId}")
    public Result updateOrderStatus(@PathVariable("orderId") String tradeNo, @RequestParam("status") String orderStatus) {
        this.orderService.updateOrderProcessStatus(Long.parseLong(tradeNo), ProcessStatus.valueOf(orderStatus));
        return Result.ok();
    }

    /**
     * orderId
     * [{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
     *
     * @param params
     * @return
     */
    @PostMapping("orderSplit")
    public String orderSplit(@RequestParam Map<String, Object> params) {
        // 订单id
        String orderId = (String) params.get("orderId");
        // 仓库id 和 skuId
        String wareSkuMapJson = (String) params.get("wareSkuMap");
        List<OrderWareVO> result = this.orderService.orderSplit(orderId, JSON.parseArray(wareSkuMapJson, WareSkuVo.class));
        return JSON.toJSONString(result);
    }
}
