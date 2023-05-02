package com.wjl.gmall.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.WebUtils;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.wjl.gmall.model.enums.OrderStatus;
import com.wjl.gmall.model.enums.PaymentStatus;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.client.OrderServiceClient;
import com.wjl.gmall.order.model.dto.OrderInfo;
import com.wjl.gmall.pay.config.properites.AlipayProperties;
import com.wjl.gmall.pay.model.dto.AliPayParamsDTO;
import com.wjl.gmall.pay.model.entity.PaymentInfo;
import com.wjl.gmall.pay.service.AliPayService;
import com.wjl.gmall.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@Service
public class AliPayServiceImpl implements AliPayService {


    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 1. 查询订单
     * 2. 保存订单信息
     * 3. 提交支付宝
     *
     * @param orderId
     * @return
     */
    @Override
    public String submitOrder(Long orderId) {
        OrderInfo orderUnpaid = orderServiceClient.getOrderUnpaid(orderId).getData();
        if (orderUnpaid == null) {
            return "订单不存在或者已经关闭";
        }

        // 保存正在在支付支付信息
        paymentService.saveUnpaidPaymentInfo(orderUnpaid, PaymentType.ALIPAY);

        // 调用支付宝界面
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setBizContent(AliPayParamsDTO.requestParamsJson(orderUnpaid.getOutTradeNo(), "0.01", orderUnpaid.getTradeBody(), "FAST_INSTANT_TRADE_PAY", "10m"));
        // 同步回调，用户支付完成之后支付宝端调用的url
        request.setReturnUrl(AlipayProperties.RETURN_PAYMENT_URL);
        // 异步回调
        request.setNotifyUrl(AlipayProperties.NOTIFY_PAYMENT_URL);
        String from = "";
        try {
            from = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return from;
    }

    /**
     * 根据支付宝的异步回调 判断订单是否成功支付
     * 成功则更新付款信息
     *
     * @param paramsMap
     * @return
     */
    @Transactional
    @Override
    public String verifySign(Map<String, String> paramsMap) {
        // System.out.println(JSON.toJSONString(paramsMap));
        boolean signVerified;
        try {
            // 验签
            signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayProperties.ALIPAY_PUBLIC_KEY, AlipayProperties.CHARSET, AlipayProperties.SIGN_TYPE);
        } catch (AlipayApiException e) {
            return "failure";
        }
        if (!signVerified) {
            return "failure";
        }
        // 商家订单号
        String outTradeNo = paramsMap.get("out_trade_no");
        // 验证总金额
        String totalAmount = paramsMap.get("total_amount");
        // app_id
        String appId = paramsMap.get("app_id");
        Optional<PaymentInfo> paymentOptional = paymentService.getPaymentInfoWithOutTradeNo(outTradeNo, PaymentType.ALIPAY);
        if (!paymentOptional.isPresent()) {
            return "failure";
        }
        PaymentInfo info = paymentOptional.get();
        // 有一个不对就是false
        // 真正的比价
        boolean eq = !(new BigDecimal(totalAmount).compareTo(info.getTotalAmount()) == 0);
        if (!info.getOutTradeNo().equals(outTradeNo) || !(new BigDecimal(totalAmount).compareTo(new BigDecimal("0.01")) == 0) || !AlipayProperties.APP_ID.equals(appId)) {
            return "failure";
        }

        String tradeStatus = paramsMap.get("trade_status");
        if (!("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus))) {
            return "failure";
        }

        String notifyId = paramsMap.get("notify_id");
        // 保证幂等性
        Boolean success = redisTemplate.opsForValue().setIfAbsent("notifyId:" + notifyId, notifyId, 25, TimeUnit.HOURS);
        try {
            if (Boolean.TRUE.equals(success)) {
                // 支付宝订单号
                String tradeNo = paramsMap.get("trade_no");
                // 设置当前支付状态
                paymentService.updatePaymentInfo(outTradeNo, PaymentType.ALIPAY, tradeNo, PaymentStatus.PAID);
            }
            return "success";
        } catch (Exception e) {
            redisTemplate.delete(notifyId);
            return "failure";
        }
    }

    /**
     * 退款接口 根据订单退款
     *
     * @param orderId
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refund(Long orderId, String userId) {
        OrderInfo orderInfo = this.orderServiceClient.getOrderInfo(orderId).getData();
        // 对象是空 用户名对不上 付款状态对不上
        if (orderInfo == null || !orderInfo.getUserId().equals(Long.parseLong(userId)) || !orderInfo.getOrderStatus().equals(OrderStatus.PAID.name())) {
            return;
        }

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        // 实际退款金额
        String refundAmount = orderInfo.getTotalAmount().toString();
        request.setBizContent(AliPayParamsDTO.requestRefundJson(orderInfo.getOutTradeNo(), "0.01", "..."));

        try {
            WebUtils.setNeedCheckServerTrusted(false);
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                // 订单状态
                this.orderServiceClient.updateOrderStatus(orderId.toString(), OrderStatus.CLOSED.name());
                // 支付记录
                this.paymentService.updatePaymentInfo(orderInfo.getOutTradeNo(), PaymentType.ALIPAY, null, PaymentStatus.CLOSED);
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("退款失败！", e);
        }
    }
}
