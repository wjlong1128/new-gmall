package com.wjl.gmall.order.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gmall.order.OrderApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@SpringBootTest(classes = OrderApplication.class)
public class OrderInfoMapperTest {

   @Autowired
   private OrderInfoMapper orderInfoMapper;

    @Test
    void testGetOrders() {
        System.out.println(orderInfoMapper.getOrdersPage(new Page<>(1L, 3L), "1").getRecords());
    }
}
