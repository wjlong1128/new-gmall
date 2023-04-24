package com.wjl.gmall.weball;

import com.wjl.gmall.product.client.config.ProductDefaultConfig;
import com.wjl.gmall.item.config.ItemDefaultConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@EnableDiscoveryClient
@ComponentScan("com.wjl.gmall")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.wjl.gmall",defaultConfiguration = {ItemDefaultConfig.class, ProductDefaultConfig.class})
public class WebAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class,args);
    }
}
