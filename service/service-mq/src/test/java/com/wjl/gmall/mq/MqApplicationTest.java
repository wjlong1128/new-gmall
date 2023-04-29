package com.wjl.gmall.mq;

import com.wjl.gmall.common.config.test.Wjl;
import com.wjl.gmall.common.service.RabbitService;
import com.wjl.gmall.common.utils.ApplicationContextHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@SpringBootTest(classes = MqApplication.class)
public class MqApplicationTest {

    @Wjl("java_home")
    private String name;

    @Test
    void test() {
        RabbitService service = ApplicationContextHolder.getBean(RabbitService.class);
        System.out.println(service);
        service.sendMessage("test.exchange","test.key1","hello aware");
        System.out.println(name);
    }
}
