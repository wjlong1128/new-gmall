package com.wjl.gamll.product.redis;

import com.wjl.gmall.product.ProductApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@SpringBootTest(classes = ProductApplication.class)
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test() {
        stringRedisTemplate.opsForValue().set("test", "test");
    }
}
