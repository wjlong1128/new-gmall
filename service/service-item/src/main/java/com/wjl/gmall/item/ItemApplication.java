package com.wjl.gmall.item;

import com.wjl.gmall.product.client.config.ProductDefaultConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@EnableFeignClients(basePackages = "com.wjl.gmall", defaultConfiguration = ProductDefaultConfig.class)
@EnableDiscoveryClient
@ComponentScan("com.wjl.gmall")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class, args);
    }
}
