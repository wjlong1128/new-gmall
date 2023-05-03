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
    /**
     * 保存未支付的记录信息
     *
     * @param orderUnpaid
     * @param paymentType
     */
    void saveUnpaidPaymentInfo(OrderInfo orderUnpaid, PaymentType paymentType);

    /**
     * 根据商家订单号获取记录
     *
     * @param outTradeNo
     * @param alipay
     * @return
     */
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
     * 根据订单id和支付类型关闭交易记录
     *
     * @param orderId
     * @param type
     */
    void closePayment(Long orderId, PaymentType type);

    /**
     * 根据订单id查询交易记录
     *
     * @param orderId
     * @return
     */
    Optional<PaymentInfo> getPaymentInfoWithOrderId(Long orderId);

    /**
     * 获取支付记录 无论支付方式
     *
     * @param orderId
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId);

    /**
     * 获取支付记录
     *
     * @param orderId
     * @param valueOf
     * @return
     */
    PaymentInfo getPaymentInfo(Long orderId, PaymentType valueOf);
}
