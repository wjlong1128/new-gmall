package com.wjl.gamll.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@EnableDiscoveryClient
@SpringBootApplication
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class,args);
    }

}
