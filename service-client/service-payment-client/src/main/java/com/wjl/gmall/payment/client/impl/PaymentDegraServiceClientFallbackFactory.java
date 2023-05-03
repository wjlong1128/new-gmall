package com.wjl.gmall.payment.client.impl;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.payment.client.PaymentServiceClient;
import com.wjl.gmall.payment.dto.PaymentInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@Component
public class PaymentDegraServiceClientFallbackFactory implements FallbackFactory<PaymentServiceClient> {
    @Override
    public PaymentServiceClient create(Throwable cause) {
        return new PaymentServiceClient() {
            @Override
            public Result<Boolean> checkPayment(Long orderId) {
                return Result.fail();
            }

            @Override
            public Result<Boolean> closePay(Long orderId) {
                return Result.fail();
            }

            @Override
            public Result<PaymentInfo> getPaymentInfo(Long orderId) {
                return Result.fail();
            }

            @Override
            public Result<PaymentInfo> getPaymentInfo(Long orderId, String type) {
                return Result.fail();
            }
        };
    }
}
