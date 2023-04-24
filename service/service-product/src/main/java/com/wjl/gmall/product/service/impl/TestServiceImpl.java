package com.wjl.gmall.product.service.impl;

import com.wjl.gmall.product.service.TestService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 *
 * yum install -y httpd-tools
 * ab -n 5000 -c 100 http://192.168.56.1:8206/admin/product/testLock
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    //@Override
    //public synchronized String  testLock() {
    //    ValueOperations<String, String> forValue = stringRedisTemplate.opsForValue();
    //    String num = forValue.get("num");
    //    if(!StringUtils.hasText(num)){
    //        return num;
    //    }
    //
    //    // 1
    //    Integer integer = Integer.valueOf(num)+1;
    //
    //    String string = integer.toString();
    //    // 实际请求5000 redis中 203 线程安全问题 1,2两步操作不是原子的存在这边改完那边覆盖
    //    // 解决问题1 加锁，防止tomcat 多线程的安全问题
    //
    //    // 2
    //    forValue.set("num",string);
    //    return string;
    //}

    // 开启两台机器 测试 ab -n 5000 -c 100 http://192.168.56.1/admin/product/testLock
    // redis中 3072 问题存在 两台机器无法用到同一个jvm进程锁
    @Override
    public String testLock() {
        try {
            redissonClient.getLock("lock").lock();
            ValueOperations<String, String> forValue = stringRedisTemplate.opsForValue();
            String num = forValue.get("num");
            if (!StringUtils.hasText(num)) {
                return num;
            }

            // 1
            Integer integer = Integer.valueOf(num) + 1;

            String string = integer.toString();
            // 实际请求5000 redis中 203 线程安全问题 1,2两步操作不是原子的存在这边改完那边覆盖

            // 2
            forValue.set("num", string);
            return string;
        } finally {
            redissonClient.getLock("lock").unlock();
        }
    }


}
