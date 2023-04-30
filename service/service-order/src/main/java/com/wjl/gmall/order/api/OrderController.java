package com.wjl.gmall.order.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.util.AuthContextHolder;
import com.wjl.gmall.model.enums.OrderStatus;
import com.wjl.gmall.order.model.entity.OrderDetail;
import com.wjl.gmall.order.model.entity.OrderInfo;
import com.wjl.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
     * 获取交易信息
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
     * 提交订单 返回订单id
     * 验证价格是否变更，变更则将变更的数据发送给cart服务更新购物扯数据
     * 调用库存服务查看库存是否足够
     *
     * @param orderInfo
     * @param request
     * @param tradeNo   防止重复提交的流水号
     * @return
     */
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo, @RequestParam("tradeNo") String tradeNo, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        if (!orderService.checkTradeNoCode(userId, tradeNo)) {
            return Result.fail().message("不能重复提交订单!");
        }
        boolean flag = true;
        for (OrderDetail item : orderInfo.getOrderDetailList()) {
            flag = orderService.checkStock(item.getSkuId().toString(), item.getSkuNum().toString());
            if (!flag) {
                return Result.fail().message(item.getSkuName() + "库存不足");
            }
        }
        // 验证价格是否变动
        boolean checked = orderService.checkedPrice(orderInfo, userId);
        if (checked) {
            return Result.fail().message("价格变更!!!");
        }
        Long orderId = orderService.submitOrder(orderInfo, userId);
        // 删除流水号
        orderService.deleteTradeNoCode(userId);
        return Result.ok(orderId);
    }

    /**
     * 我的订单
     * @param request
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("auth/{page}/{limit}")
    public Result<IPage<OrderInfo>> getOrders(HttpServletRequest request, @PathVariable("page") Long page, @PathVariable("limit") Long limit) {
        String userId = AuthContextHolder.getUserId(request);
        IPage<OrderInfo> orderInfoIPage = orderService.getOrders(userId,page,limit);
        return Result.ok(orderInfoIPage);
    }

    /**
     *  订单付款页面信息
     * @param orderId
     * @return
     */
    @GetMapping("inner/getOrderInfo/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable("orderId") Long orderId,HttpServletRequest request){
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        return Result.ok(orderInfo);
    }
    /**
     *  订单付款页面信息
     * @param orderId
     * @return
     */
    @GetMapping("inner/getOrderInfoUnpaid/{orderId}")
    public Result<OrderInfo> getOrderUnpaid(@PathVariable("orderId") Long orderId,HttpServletRequest request){
        OrderInfo orderInfo = orderService.getOrderInfoWithStatus(orderId, OrderStatus.UNPAID);
        return Result.ok(orderInfo);
    }
}
