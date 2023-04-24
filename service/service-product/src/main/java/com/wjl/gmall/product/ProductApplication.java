package com.wjl.gmall.product;


import com.wjl.gamll.file.client.FileServiceClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date $DATE
 * @description
 */

@EnableAspectJAutoProxy
@EnableFeignClients(
        clients = {FileServiceClient.class}
)
@EnableDiscoveryClient
@SpringBootApplication
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}