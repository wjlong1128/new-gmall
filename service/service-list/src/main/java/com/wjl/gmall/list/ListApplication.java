package com.wjl.gmall.list;

import com.wjl.gmall.product.client.config.ProductDefaultConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
@EnableFeignClients(basePackages = "com.wjl.gmall",defaultConfiguration = ProductDefaultConfig.class)
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ListApplication {

    public static void main(String[] args) {
        SpringApplication.run(ListApplication.class,args);
    }

}
