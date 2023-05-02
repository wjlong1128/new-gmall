package com.wjl.gmall.pay.config.properites;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
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
public class AlipayProperties implements InitializingBean {

    public static final String SIGN_TYPE = "RSA2";
    public static String ALIPAY_PUBLIC_KEY;
    public static String ALIPAY_URL;
    public static String APP_ID;
    public static String APP_PRIVATE_KEY;
    public static String NOTIFY_PAYMENT_URL;
    public static String RETURN_ORDER_URL;
    public static String RETURN_PAYMENT_URL;

    public static final String CHARSET = "utf-8";

    private String alipayPublicKey;
    private String alipayUrl;
    private String appId;
    private String appPrivateKey;
    private String notifyPaymentUrl;
    private String returnOrderUrl;
    private String returnPaymentUrl;

    @Override
    public void afterPropertiesSet() throws Exception {
        ALIPAY_PUBLIC_KEY = alipayPublicKey;
        ALIPAY_URL = alipayUrl;
        APP_ID = appId;
        APP_PRIVATE_KEY = appPrivateKey;
        NOTIFY_PAYMENT_URL = notifyPaymentUrl;
        RETURN_ORDER_URL = returnOrderUrl;
        RETURN_PAYMENT_URL = returnPaymentUrl;
    }
}
