package com.wjl.gmall.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.wjl.gmall.pay.config.properites.AlipayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/1
 * @description
 */
@Configuration
public class AliPayClientConfig {

    @Bean
    public AlipayClient alipayClient(AlipayProperties properties) {
        DefaultAlipayClient defaultAlipayClient = new DefaultAlipayClient(
                properties.getAlipayUrl(),
                properties.getAppId(),
                properties.getAppPrivateKey(),
                "json",
                "UTF-8",
                properties.getAlipayPublicKey(),
                "RSA2"
        );
        return defaultAlipayClient;
    }

}
