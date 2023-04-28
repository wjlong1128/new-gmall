package com.wjl.gmall.order;

import com.wjl.gmall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@SpringBootTest(classes = OrderApplication.class)
public class OrderApplicationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Test
    void testRequest() {
        System.out.println(orderService.checkStock("21", "101"));
    }

    @Test
    void testMaxThread() {
        System.out.println(executor.getMaximumPoolSize());
    }
}
