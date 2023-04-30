package com.wjl.gmall.pay.config.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    private String alipayPublicKey;
    private String alipayUrl;
    private String appId;
    private String appPrivateKey;
    private String notifyPaymentUrl;
    private String returnOrderUrl;
    private String returnPaymentUrl;
}
