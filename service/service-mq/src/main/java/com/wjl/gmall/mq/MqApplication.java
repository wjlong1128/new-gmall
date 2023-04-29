package com.wjl.gmall.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@EnableDiscoveryClient
@ComponentScan("com.wjl.gmall")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MqApplication {
    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class, args);
    }
}
