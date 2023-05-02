package com.wjl.gmall.pay.service;

import java.util.Map;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
public interface AliPayService {

    String submitOrder(Long orderId);

    String verifySign(Map<String, String> paramsMap);

    void refund(Long orderId, String userId);
}
