package com.wjl.gmall.weball.web;

import com.wjl.gmall.order.client.OrderServiceClient;
import com.wjl.gmall.order.model.dto.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@Controller
public class PayController {

    @Autowired
    private OrderServiceClient orderServiceClient;

    @GetMapping("pay.html")
    public String pay(Long orderId, Model model) {
        OrderInfo data = orderServiceClient.getOrderInfo(orderId).getData();
        model.addAttribute("orderInfo", data);
        return "payment/pay";
    }

    /**
     * 跳转成功界面
     *
     * @return
     */
    @GetMapping("pay/success.html")
    public String paySuccess() {
        return "payment/success";
    }

}
