package com.wjl.gmall.pay.service.impl;

import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.client.OrderServiceClient;
import com.wjl.gmall.order.model.dto.OrderInfo;
import com.wjl.gmall.pay.service.PayService;
import com.wjl.gmall.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@Service
public class PayServiceImpl implements PayService {


    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private PaymentService paymentService;

    /**
     *  1. 查询订单
     *  2. 保存订单信息
     *  3. 提交支付宝
     * @param orderId
     * @return
     */
    @Override
    public String submitOrder(Long orderId) {
        OrderInfo orderUnpaid = orderServiceClient.getOrderUnpaid(orderId).getData();
        if (orderUnpaid == null){
            return "订单不存在或者已经关闭";
        }

        // 保存支付信息
        paymentService.saveUnpaidPaymentInfo(orderUnpaid, PaymentType.ALIPAY);
        return null;
    }
}
