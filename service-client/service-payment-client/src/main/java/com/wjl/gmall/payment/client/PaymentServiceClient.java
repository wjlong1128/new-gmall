package com.wjl.gmall.payment.client;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.payment.client.impl.PaymentDegraServiceClientFallbackFactory;
import com.wjl.gmall.payment.dto.PaymentInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@FeignClient(value = "service-payment", fallbackFactory = PaymentDegraServiceClientFallbackFactory.class)
public interface PaymentServiceClient {

    /**
     * 查询支付宝交易记录
     *
     * @param orderId
     * @return
     */
    @GetMapping("api/payment/alipay/checkPayment/{orderId}")
    public Result<Boolean> checkPayment(@PathVariable("orderId") Long orderId);


    /**
     * 支付宝关闭交易 用户已经扫码 但是未支付
     *
     * @param orderId
     * @return
     */
    @GetMapping("api/payment/alipay/closePay/{orderId}")
    public Result<Boolean> closePay(@PathVariable("orderId") Long orderId);

    /**
     * 查询是否有支付记录 无论支付方式
     *
     * @param orderId
     * @return
     */
    @GetMapping("api/payment/getPaymentInfo/{orderId}")
    public Result<PaymentInfo> getPaymentInfo(@PathVariable("orderId") Long orderId);


    /**
     * 查询是否有支付记录
     *
     * @param orderId
     * @return
     */
    @GetMapping("api/payment/getPaymentInfo/{orderId}/{type}")
    public Result<PaymentInfo> getPaymentInfo(@PathVariable("orderId") Long orderId, @PathVariable("type") String type);
}
