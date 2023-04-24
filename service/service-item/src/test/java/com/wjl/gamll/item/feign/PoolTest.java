package com.wjl.gamll.item.feign;

import com.wjl.gamll.item.ItemApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 */
@SpringBootTest(classes = ItemApplication.class)
public class PoolTest {

    @Autowired
    private ThreadPoolTaskExecutor itemThreadPoolTaskExecutor;

    //@Autowired
    //private ThreadPoolExecutor itemThreadPoolExecutor;
    //
    @Test
    void testPool() {
        System.out.println(itemThreadPoolTaskExecutor.getMaxPoolSize());
    }
}
