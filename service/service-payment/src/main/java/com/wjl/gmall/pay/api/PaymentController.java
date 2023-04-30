package com.wjl.gmall.pay.api;

import com.wjl.gmall.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@RequestMapping("api/payment")
@RestController
public class PaymentController {

    @Autowired
    private PayService payService;

    /**
     * 获取请求
     * @param orderId
     * @return
     */
    @GetMapping("/alipay/submit/{orderId}")
    public String submitOrder(@PathVariable("orderId") Long orderId){
        String alipayFromPage = payService.submitOrder(orderId);
        return alipayFromPage;
    }

}
