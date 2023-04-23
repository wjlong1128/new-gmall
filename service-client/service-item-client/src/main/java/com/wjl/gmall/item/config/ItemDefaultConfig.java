package com.wjl.gmall.item.config;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */

import com.wjl.gmall.item.client.impl.ItemDegradeServiceClientFallbackFactory;
import org.springframework.context.annotation.Bean;

public class ItemDefaultConfig {
    @Bean
    public ItemDegradeServiceClientFallbackFactory itemDegradeServiceClientFallbackFactory() {
        return new ItemDegradeServiceClientFallbackFactory();
    }
}
