package com.wjl.gmall.weball.web;

import com.wjl.gmall.order.client.OrderServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@Controller
public class OrderController {

    @Autowired
    private OrderServiceClient orderServiceClient;

    /**
     * 结算页面
     */
    @GetMapping("/trade.html")
    public String trade(Model model) {
        Map<String, Object> tradeData = orderServiceClient.trade().getData();
        model.addAllAttributes(tradeData);
        return "order/trade";
    }

    /**
     * 跳转我的订单
     * @param model
     * @return
     */
    @GetMapping("/myOrder.html")
    public String myOrder(Model model) {
        return "order/myOrder";
    }

}
