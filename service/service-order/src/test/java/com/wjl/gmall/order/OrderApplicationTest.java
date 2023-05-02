package com.wjl.gmall.order;

import com.wjl.gmall.order.model.entity.OrderInfo;
import com.wjl.gmall.order.model.vo.OrderWareVO;
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

    @Test
    void testtoJson() {
        OrderInfo orderInfo = this.orderService.getOrderInfo(54L);
        System.out.println(new OrderWareVO(orderInfo,1L).toJson());
    }
}
