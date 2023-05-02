package com.wjl.gmall.pay;

import com.wjl.gmall.pay.service.AliPayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@SpringBootTest(classes = PaymentApplication.class)
public class PaymentApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;




    @Test
    void contextLoad() {
        AliPayService bean = applicationContext.getBean(AliPayService.class);
        System.out.println(bean);
        System.out.println(bean.getClass());
    }

}
