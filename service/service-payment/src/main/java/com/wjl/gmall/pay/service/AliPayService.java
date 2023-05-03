package com.wjl.gmall.pay.service;

import java.util.Map;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
public interface AliPayService {

    /**
     *  提交订单
     * @param orderId
     * @return
     */
    String submitOrder(Long orderId);

    /**
     *  支付宝回调验证
     * @param paramsMap
     * @return
     */
    String verifySign(Map<String, String> paramsMap);

    /**
     * 支付宝退款
     * @param orderId
     * @param userId
     */
    void refund(Long orderId, String userId);

    /**
     *  支付宝交易记录是否存在
     * @param orderId
     * @return
     */
    boolean checkPayment(Long orderId);

    /**
     *  关闭支付宝交易记录
     * @param orderId
     * @return
     */
    boolean closePay(Long orderId);
}
