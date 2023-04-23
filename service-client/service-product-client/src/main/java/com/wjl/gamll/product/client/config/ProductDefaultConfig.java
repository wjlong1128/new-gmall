package com.wjl.gamll.product.client.config;

import com.wjl.gamll.product.client.impl.ProductServiceDegradeClientFallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@Configuration
public class ProductDefaultConfig {

    @Bean
    public ProductServiceDegradeClientFallbackFactory productServiceDegradeClientFallbackFactory(){
        return new ProductServiceDegradeClientFallbackFactory();
    }

}
