package com.wjl.gmall.common.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "gmall.default-thread-pool")
public class GmallThreadPoolConfig {

    private Integer coreSize = 12;
    private Integer maxSize = 24;
    private Long keepAliveTime = 30L;
    private Integer queueSize = 1024;
    private TimeUnit unit = TimeUnit.SECONDS;

    @ConditionalOnMissingBean(ThreadPoolExecutor.class)
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                keepAliveTime,
                unit,
                new ArrayBlockingQueue<>(queueSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
