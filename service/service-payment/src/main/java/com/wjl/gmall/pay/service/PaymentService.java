package com.wjl.gmall.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.model.dto.OrderInfo;
import com.wjl.gmall.pay.model.entity.PaymentInfo;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
public interface PaymentService extends IService<PaymentInfo> {
    void saveUnpaidPaymentInfo(OrderInfo orderUnpaid, PaymentType paymentType);
}
