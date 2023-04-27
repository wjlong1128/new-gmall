package com.wjl.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@EnableDiscoveryClient
@ComponentScan("com.wjl.gmall")
@EnableFeignClients(basePackages = "com.wjl.gmall")
@SpringBootApplication
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class,args);
    }
}
