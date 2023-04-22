package com.wjl.gmall.product;

import com.wjl.gamll.feign.client.FileServiceClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date $DATE
 * @description
 */
@EnableFeignClients(
        clients = {FileServiceClient.class}
)
@SpringBootApplication
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}