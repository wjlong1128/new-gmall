package com.wjl.gamll.product;

import com.wjl.gmall.product.ProductApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 */
@SpringBootTest(classes = ProductApplication.class)
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    void test() {
        redisTemplate.opsForHash().put("person","name",new Object());
    }
}
