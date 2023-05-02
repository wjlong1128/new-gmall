package com.wjl.gmall.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.gmall.model.enums.PaymentStatus;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.model.dto.OrderInfo;
import com.wjl.gmall.pay.model.entity.PaymentInfo;

import java.util.Optional;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
public interface PaymentService extends IService<PaymentInfo> {
    void saveUnpaidPaymentInfo(OrderInfo orderUnpaid, PaymentType paymentType);

    Optional<PaymentInfo> getPaymentInfoWithOutTradeNo(String outTradeNo, PaymentType alipay);

    /**
     * 根据订单号修改订单状态
     *
     * @param outTradeNo 订单和支付宝唯一的号
     * @param type       支付类型
     * @param orderId    支付宝订单号
     * @param status     订单状态
     */
    void updatePaymentInfo(String outTradeNo, PaymentType type, String orderId, PaymentStatus status);

    /**
     *  根据订单id和支付类型关闭交易记录
     * @param orderId
     * @param type
     */
    void closePayment(Long orderId, PaymentType type);
}
